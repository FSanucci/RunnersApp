package com.app.runners.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import com.app.runners.model.Resource;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.app.runners.utils.Constants.KEY_RESOURCE;

/**
 * Created by Fede_CC on 22/09/2018.
 */

public class ResourcesHelper {

    private static final String TAG = "RES_HELPER";
    private static final String VIDEO_EXT = ".mp4";
    private static final String IMAGE_EXT = ".jpg";
    private static final String VIDEO_TMP = "temp.mp4";
    private static final String IMAGE_TMP = "temp.jpg";

    private String[] fileList;
    private List<String> filesToDelete = new ArrayList<String>();
    private String filenameTemp = "";

    private static ResourcesHelper mInstance = new ResourcesHelper();

    public static synchronized ResourcesHelper getInstance() {
        return mInstance;
    }

    public void downloadResData(ArrayList<Resource> resources, Context ctx){
        Log.e(TAG, "downloadResData() ---------------------- ");
/*
        fileList = ctx.fileList();
        Collections.addAll(filesToDelete, fileList);
        if (!resources.isEmpty()){
            Resource res = resources .remove(0);
            downloadResource(resources, ctx, res);
        } else {
            deleteOldFiles(ctx);
        }
*/

        fileList = ctx.fileList();
        Collections.addAll(filesToDelete, fileList);
        for (String file : fileList){
            if (!file.contains(VIDEO_EXT) && !file.contains(IMAGE_EXT)){
                filesToDelete.remove(file);
            }
        }

        deleteOldFiles(ctx, resources);

        if (!resources.isEmpty()){
            Resource res = resources .remove(0);
            downloadResource(resources, ctx, res);
        }

    }

    private void deleteOldFiles(final Context ctx, ArrayList<Resource> resources){
        Log.e(TAG, "deleteOldFiles() ---------------------- ");

        for (Resource resource : resources){
            String fileName = resource.hash;

            if (resource.type.equals("video")){
                fileName = fileName + VIDEO_EXT;
            } else if (resource.type.equals("image")){
                fileName = fileName + IMAGE_EXT;
            }

            filesToDelete.remove(fileName);
        }

        for(String file : filesToDelete){

            if (file.contains(VIDEO_EXT) || file.contains(IMAGE_EXT)){
                Log.e(TAG, "File to Delete: " + file);

                Storage.getInstance().remove(KEY_RESOURCE);

                File fileToDelete = ctx.getFileStreamPath(file);
                fileToDelete.delete();
            }
        }

    }

    private void downloadResource(final ArrayList<Resource> resources, final Context ctx, final Resource res){
        Log.e(TAG, "downloadResource() ---------------------- ");

        String filename = res.hash;
        switch (res.type){
            case "video":
                filename = filename + VIDEO_EXT;
                filenameTemp = VIDEO_TMP;
                break;
            case "image":
                filename = filename + IMAGE_EXT;
                filenameTemp = IMAGE_TMP;
                break;
        }

        boolean fileExist = false;
        for (String fileNameSt : fileList){
            if (fileNameSt.equals(filename)){
                fileExist = true;
                filesToDelete.remove(fileNameSt);
                break;
            }
        }

        Log.e(TAG, "File: " + filename + " | ExistsInStorage: " + fileExist);

        if (!fileExist){
            final String fileToWrite = filename;

            new Thread(new Runnable() {
                public void run() {
                    DownloadFiles(resources, ctx, res, fileToWrite);
                }
            }).start();
        } else if (!resources.isEmpty()){
            Resource resource = resources.remove(0);
            downloadResource(resources, ctx, resource);
        }
/*
        String fileContents = res.url;
        FileOutputStream outputStream;

        try {
            outputStream = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
*/
    }

    private void DownloadFiles(final ArrayList<Resource> resources, final Context ctx, final Resource res, final String filename){
        Log.e(TAG, "DownloadFiles() ---------------------- ");

        Log.e(TAG, "Downloading: " + filename + ".....");

        try {
            URL u = new URL(res.url);
            InputStream is = u.openStream();

            DataInputStream dis = new DataInputStream(is);

            byte[] buffer = new byte[1024];
            int length;
/*
            FileOutputStream fos = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
            while ((length = dis.read(buffer))>0) {
                fos.write(buffer, 0, length);
            }
*/
            FileOutputStream fos = ctx.openFileOutput(filenameTemp, Context.MODE_PRIVATE);
            while ((length = dis.read(buffer))>0) {
                fos.write(buffer, 0, length);
            }

            File oldfile = ctx.getFileStreamPath(filenameTemp);
            File newfile = ctx.getFileStreamPath(filename);
            oldfile.renameTo(newfile);

            Log.e(TAG, "Finish to Download file (" + filename + ")!");

            if (!resources.isEmpty()){
                Resource resource = resources.remove(0);
                downloadResource(resources, ctx, resource);
            } else {
                //deleteOldFiles2(ctx);
            }

        } catch (MalformedURLException mue) {
            Log.e("SYNC getUpdate", "malformed url error", mue);
        } catch (IOException ioe) {
            Log.e("SYNC getUpdate", "io error", ioe);
        } catch (SecurityException se) {
            Log.e("SYNC getUpdate", "security error", se);
        }
    }
/*
    private void deleteOldFiles2(Context ctx){
        Log.e(TAG, "deleteOldFiles() ---------------------- ");

        for(String file : filesToDelete){

            if (file.contains(VIDEO_EXT) || file.contains(IMAGE_EXT)){
                File fileToDelete = ctx.getFileStreamPath(file);
                fileToDelete.delete();
            }
        }
    }
*/
}
