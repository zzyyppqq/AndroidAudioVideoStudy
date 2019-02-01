package com.zyp.androidaudiovideostudy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.util.Log;

public class FileHelper {
    private static final String TAG = "FileHelper";

    public static String getSDCardDirPath() {
        return android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static boolean isSDCardExist() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    public static boolean copyFolder(String srcFolderFullPath, String destFolderFullPath) {
        Log.d(TAG, "copyFolder " + "srcFolderFullPath-" + srcFolderFullPath + " destFolderFullPath-" + destFolderFullPath);
        try {
            (new File(destFolderFullPath)).mkdirs();
            File file = new File(srcFolderFullPath);
            String[] files = file.list();
            File temp = null;
            for (int i = 0; i < files.length; i++) {
                if (srcFolderFullPath.endsWith(File.separator)) {
                    temp = new File(srcFolderFullPath + files[i]);
                } else {
                    temp = new File(srcFolderFullPath + File.separator + files[i]);
                }
                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    copyFile(input, destFolderFullPath + "/" + (temp.getName()).toString());
                }
                if (temp.isDirectory()) {
                    copyFolder(srcFolderFullPath + "/" + files[i], destFolderFullPath + "/" + files[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "copyFolder " + e.getMessage());
            return false;
        }
        return true;
    }

    public static boolean copyFile(InputStream ins, String destFileFullPath) {
        Log.d(TAG, "copyFile " + "destFileFullPath-" + destFileFullPath);
        FileOutputStream fos = null;
        try {
            File file = new File(destFileFullPath);
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[8192];
            int count = 0;
            while ((count = ins.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "copyFile " + e.getMessage());
            return false;
        } finally {
            try {
                fos.close();
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "copyFile " + e.getMessage());
            }
        }
    }

    public static void deleteFolder(String targetFolderFullPath) {
        Log.d(TAG, "deleteFolder " + "targetFolderFullPath-" + targetFolderFullPath);
        File file = new File(targetFolderFullPath);
        if (!file.exists()) {
            return;
        }
        String[] files = file.list();
        File temp = null;
        for (int i = 0; i < files.length; i++) {
            if (targetFolderFullPath.endsWith(File.separator)) {
                temp = new File(targetFolderFullPath + files[i]);
            } else {
                temp = new File(targetFolderFullPath + File.separator + files[i]);
            }
            if (temp.isFile()) {
                deleteFile(targetFolderFullPath + "/" + (temp.getName()).toString());
            }
            if (temp.isDirectory()) {
                deleteFolder(targetFolderFullPath + "/" + files[i]);
            }
        }
        file.delete();
    }

    public static void deleteFile(String targetFileFullPath) {
        Log.d(TAG, "deleteFolder " + "targetFileFullPath-" + targetFileFullPath);
        File file = new File(targetFileFullPath);
        file.delete();
    }


    public static void copyFileFromAssets(Context context, String assetsFilePath, String targetFileFullPath) {
        Log.d(TAG, "copyFileFromAssets ");
        InputStream assestsFileImputStream;
        try {
            assestsFileImputStream = context.getAssets().open(assetsFilePath);
            FileHelper.copyFile(assestsFileImputStream, targetFileFullPath);
        } catch (IOException e) {
            Log.d(TAG, "copyFileFromAssets " + "IOException-" + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void copyFolderFromAssets(Context context, String rootDirFullPath, String targetDirFullPath) {
        Log.d(TAG, "copyFolderFromAssets " + "rootDirFullPath-" + rootDirFullPath + " targetDirFullPath-" + targetDirFullPath);
        try {
            String[] listFiles = context.getAssets().list(rootDirFullPath);
            for (String string : listFiles) {
                Log.d(TAG, "name-" + rootDirFullPath + "/" + string);
                if (isFileByName(string)) {
                    copyFileFromAssets(context, rootDirFullPath + "/" + string, targetDirFullPath + "/" + string);
                } else {
                    String childRootDirFullPath = rootDirFullPath + "/" + string;
                    String childTargetDirFullPath = targetDirFullPath + "/" + string;
                    new File(childTargetDirFullPath).mkdirs();
                    copyFolderFromAssets(context, childRootDirFullPath, childTargetDirFullPath);
                }
            }
        } catch (IOException e) {
            Log.d(TAG, "copyFolderFromAssets " + "IOException-" + e.getMessage());
            Log.d(TAG, "copyFolderFromAssets " + "IOException-" + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private static boolean isFileByName(String string) {
        if (string.contains(".")) {
            return true;
        }
        return false;
    }
}
