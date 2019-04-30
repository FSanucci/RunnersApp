package com.app.runners.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.app.runners.BuildConfig;
import com.app.runners.R;
import com.app.runners.rest.RestConstants;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by sergiocirasa on 21/8/17.
 */

public class ImageProvider {

    public static final String AUTHORITY = "com.app.fileprovider";
    public interface ImageProviderListener {
        public void didSelectImage(String path);
    }


    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;

    private Activity mActivity;
    private Fragment mFragment;
    private PermissionsHelper mPermissionHelper;
    private String mCurrentPhotoPath;
    private ImageProviderListener mListener;

    public ImageProvider(Activity activity) {
        mActivity = activity;
        mPermissionHelper = new PermissionsHelper(mActivity);
    }

    public ImageProvider(Fragment fragment){
        mActivity = fragment.getActivity();
        mFragment = fragment;
        mPermissionHelper = new PermissionsHelper(mActivity);
    }

    public void setImageProviderListener(ImageProviderListener listener){
        mListener = listener;
    }

    public void showImagePicker() {

        if (!PermissionsHelper.checkPermissionForCamera()) {
            mPermissionHelper.requestPermissionForCamera();
            return;
        }

        final CharSequence[] options = new CharSequence[]{mActivity.getResources().getString(R.string.ip_picker_image_source_gallery_option), mActivity.getResources().getString(R.string.ip_picker_image_source_camera_option)};
        int title = R.string.ip_picker_image_source_title;

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(mActivity.getResources().getString(title));
        builder.setItems(options,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0: {
                                dispatchGalleryIntent();
                                break;
                            }
                            case 1: {
                                dispatchTakePictureIntent();
                                break;
                            }
                            default:
                                break;
                        }
                    }
                });

        builder.show();
    }

    public void showGalleryPicker(){

        if (!PermissionsHelper.checkPermissionForCamera()) {
            mPermissionHelper.requestPermissionForCamera();
            return;
        }
        dispatchGalleryIntent();
    }

    private File createTempImageFile() throws IOException {
        String imageFileName = "temp_image";
        //File storageDir = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = new File(mActivity.getExternalFilesDir(null), "images");
        if(!storageDir.exists())
            storageDir.mkdir();

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private Uri getImageUri(){
        File photoFile = null;
        try {
            photoFile = createTempImageFile();
        } catch (IOException ex) {
            Log.e("e","error");
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(mActivity,
                    AUTHORITY,
                    photoFile);
            return photoURI;
        }
        return null;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            Uri photoUri = getImageUri();
            if(photoUri!=null){
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                if (mFragment != null){
                    mFragment.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } else {
                    mActivity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }else{
            //Camera not available
        }
    }


    private void dispatchGalleryIntent(){

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        Intent chooser = Intent.createChooser(galleryIntent, mActivity.getResources().getString(R.string.image_source_title));
        mActivity.startActivityForResult(chooser, REQUEST_IMAGE_GALLERY);
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bitmap photo = BitmapFactory.decodeFile(mCurrentPhotoPath);
            photo = Bitmap.createScaledBitmap(photo, 400, 400, false);

            try { //Lógica que debería detectar la rotación de la foto antes de subirla y acomodarla en caso de ser necesario
                ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                Bitmap rotatedBitmap = null;
                switch(orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(photo, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(photo, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(photo, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotatedBitmap = photo;
                }

                if (rotatedBitmap != null){
                    photo = rotatedBitmap;
                }

            } catch (IOException e){
                e.printStackTrace();
            }
/*
            Matrix mat = new Matrix();
            mat.postRotate(90);
            Bitmap bmpRotate = Bitmap.createBitmap(photo, 0, 0,
                    photo.getWidth(), photo.getHeight(),
                    mat, true);
*/
            File dest = new File(mCurrentPhotoPath, "");

            try {
                FileOutputStream out = new FileOutputStream(dest);
                //bmpRotate.compress(Bitmap.CompressFormat.PNG, 70, out);
                photo.compress(Bitmap.CompressFormat.PNG, 70, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (mListener != null)
                mListener.didSelectImage(mCurrentPhotoPath);
        }else if(requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK){
            Uri selectedImage = data.getData();

            File photoFile = null;
            try {
                photoFile = createTempImageFile();
            } catch (IOException ex) {
                Log.e("e","error");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                savefile(selectedImage,photoFile.getAbsolutePath());

                try {
                    Bitmap photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                    photo = Bitmap.createScaledBitmap(photo, 400, 400, false);
                    File dest = new File(photoFile.getAbsolutePath(), "");

                    FileOutputStream out = new FileOutputStream(dest);
                    photo.compress(Bitmap.CompressFormat.PNG, 70, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mListener.didSelectImage(photoFile.getAbsolutePath());
            }
        }
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == PermissionsHelper.CAMERA_PERMISSION_REQUEST_CODE) {
                showImagePicker();
            }
        }
    }

    private void savefile(Uri sourceuri, String outputPath){
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            InputStream imageStream = mActivity.getContentResolver().openInputStream(sourceuri);
            bis = new BufferedInputStream(imageStream);
            bos = new BufferedOutputStream(new FileOutputStream(outputPath, false));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while(bis.read(buf) != -1);
        } catch (IOException e) {

        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {

            }
        }
    }

}


