package com.app.runners.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.runners.R;
import com.app.runners.manager.UserController;
import com.app.runners.model.Documentation;
import com.app.runners.model.User;
import com.app.runners.rest.RestApiServices;
import com.app.runners.rest.RestConstants;
import com.app.runners.rest.core.ParserUtils;
import com.app.runners.utils.AppController;
import com.app.runners.utils.DialogHelper;
import com.app.runners.utils.ImageProvider;
import com.facebook.drawee.view.SimpleDraweeView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;


public class UserDocsFragment extends Fragment {

    private final static String TAG = "USRDOC_FRG";

    private TextView mProfileTextView;
    private TextView mAntecedenteTextView;
    private TextView mDJTextView;
    private View mMainContainer;
    private SimpleDraweeView mProfilePreview;
    private SimpleDraweeView mDJPreview;
    private SimpleDraweeView mAntecedentesPreview;
    private ImageProvider mImageProvider;
    private Bitmap mSelectedImage;
    //private boolean mPickingDJ;
    private int cameraAction = -1;
    protected ProgressDialog pDialog;
    private Context mContext;
    private final static int MAX_IMAGE_DIMENSION = 512;

    public UserDocsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_docs, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mMainContainer = view.findViewById(R.id.scroll);
        mProfilePreview = (SimpleDraweeView) view.findViewById(R.id.profile);
        mAntecedentesPreview = (SimpleDraweeView) view.findViewById(R.id.antecedentes);
        mDJPreview = (SimpleDraweeView) view.findViewById(R.id.dj);
        mProfileTextView = (TextView) view.findViewById(R.id.profile_title);
        mAntecedenteTextView = (TextView) view.findViewById(R.id.antecedentes_title);
        mDJTextView = (TextView) view.findViewById(R.id.dj_title);

        mContext = view.getContext();

        updateUserContent();
        updateUserProfile();

        View btn1 = view.findViewById(R.id.antecedentes_button);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mPickingDJ = false;
                cameraAction = 1;
                showCameraPicker();
            }
        });

        View btn2 = view.findViewById(R.id.dj_button);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mPickingDJ = true;
                cameraAction = 2;
                showCameraPicker();
            }
        });

        View btn3 = view.findViewById(R.id.profile_button);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraAction = 3;
                showCameraPicker();
                //showGalleryPicker();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mImageProvider = new ImageProvider(getActivity());
        mImageProvider.setImageProviderListener(new ImageProvider.ImageProviderListener() {
            @Override
            public void didSelectImage(String path) {
                setPicture(path);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void showCameraPicker(){
        mImageProvider.showImagePicker();
    }

    private void showGalleryPicker(){
        mImageProvider.showGalleryPicker();
    }

    private void setPicture(String photoPath) {

        showProgressDialog(R.string.uploading);

        switch (cameraAction){
            case 1:
                cameraAction = -1;
                UserController.getInstance().uploadSwornStatement(photoPath, new Response.Listener<Void>() {
                    @Override
                    public void onResponse(Void response) {
                        dismissProgressDialog();
                        updateUserContent();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgressDialog();
                        DialogHelper.showMessage(getActivity(),R.string.uploading_error);
                    }
                });
                break;
            case 2:
                cameraAction = -1;
                UserController.getInstance().uploadMedicalCertificate(photoPath, new Response.Listener<Void>() {
                    @Override
                    public void onResponse(Void response) {
                        dismissProgressDialog();
                        updateUserContent();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgressDialog();
                        DialogHelper.showMessage(getActivity(),R.string.uploading_error);
                    }
                });
                break;
            case 3:
                cameraAction = -1;
                UserController.getInstance().uploadProfilePicture(photoPath, new Response.Listener<Void>() {
                    @Override
                    public void onResponse(Void response) {
                        dismissProgressDialog();
                        updateUserProfile();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgressDialog();
                        DialogHelper.showMessage(getActivity(),R.string.uploading_error);
                    }
                });
                break;
            default:
                Log.e(TAG, "CameraAction code unknown: " + cameraAction);
                cameraAction = -1;
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mImageProvider.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mImageProvider.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    protected void showProgressDialog(int message) {
        try {
            if(pDialog!=null)
                pDialog.dismiss();

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getResources().getString(message));
            pDialog.setCancelable(true);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
        } catch (Throwable e) {
        }
    }

    protected void dismissProgressDialog() {
        try {
            if (pDialog != null) {
                pDialog.dismiss();
            }
        } catch (Throwable e) {
        }
    }


    private void updateUserContent(){

        Documentation doc = UserController.getInstance().getSignedUser().documentation;
        if(doc!=null){
            mAntecedenteTextView.setText(AppController.getInstance().getString(R.string.user_doc_antecedente));
            mDJTextView.setText(AppController.getInstance().getString(R.string.user_doc_dj));

            if(doc.medicalCertificateExpirationDate!=null){
                Date date = ParserUtils.parseDate(doc.medicalCertificateExpirationDate);
                if(date!=null) {
                    String dd = ParserUtils.parseDate(date,"dd/MM/yyyy");
                    String title = AppController.getInstance().getString(R.string.user_doc_antecedente)+" (Vencimiento: "+dd+")";
                    int start = AppController.getInstance().getString(R.string.user_doc_antecedente).length();
                    int end = title.length();
                    SpannableString ss1=  new SpannableString(title);
                    ss1.setSpan(new RelativeSizeSpan(0.75f), start,end, 0); // set size
                    if(date.before(new Date())) {
                        ss1.setSpan(new ForegroundColorSpan(Color.RED), start, end, 0);// set color
                    }
                    mAntecedenteTextView.setText(ss1);
                }
            }

            if(doc.swornStatementExpirationDate!=null){
                Date date = ParserUtils.parseDate(doc.swornStatementExpirationDate);
                if(date!=null) {
                    String dd = ParserUtils.parseDate(date,"dd/MM/yyyy");
                    String title = AppController.getInstance().getString(R.string.user_doc_dj)+" (Vencimiento: "+dd+")";
                    int start = AppController.getInstance().getString(R.string.user_doc_dj).length();
                    int end = title.length();
                    SpannableString ss1=  new SpannableString(title);
                    ss1.setSpan(new RelativeSizeSpan(0.75f), start,end, 0); // set size
                    if(date.before(new Date())) {
                        ss1.setSpan(new ForegroundColorSpan(Color.RED), start, end, 0);// set color
                    }
                    mDJTextView.setText(ss1);
                }
            }

            if(doc.getSwornStatementPath() != null) {
                Uri uri1 = Uri.parse(doc.getSwornStatementPath());
                if (uri1 != null)
                    mDJPreview.setImageURI(uri1);
            }

            if(doc.getMedicalCertificatePath() != null) {
                Uri uri2 = Uri.parse(doc.getMedicalCertificatePath());
                if (uri2 != null)
                    mAntecedentesPreview.setImageURI(uri2);
            }else{


            }
        }
    }

    private void updateUserProfile(){
        User user = UserController.getInstance().getSignedUser();
        if (user.profile.profilePictureImage != null){
            String uriPath = RestConstants.IMAGE_HOST + user.profile.profilePictureImage;
            Uri uri3 = Uri.parse(uriPath);
            if (uri3 != null){
                mProfilePreview.setImageURI(uri3);
            }
        }
    }
}
