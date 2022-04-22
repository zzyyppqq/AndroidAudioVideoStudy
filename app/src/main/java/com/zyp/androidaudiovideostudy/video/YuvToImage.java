package com.zyp.androidaudiovideostudy.video;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class YuvToImage {
    private int count = 0;

    public void image(final byte[] data, final int width,int height) {

        try {
            YuvImage image = new YuvImage(data, ImageFormat.NV21, width, height, null);
            Bitmap bmp = null;
            Log.d("eric", "count : " + count);
            if (count <= 50 && image != null) {
                Log.d("eric", "enabled");
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                image.compressToJpeg(new Rect(0, 0, width, height), 80, bos);
                bmp = BitmapFactory.decodeByteArray(bos.toByteArray(), 0, bos.size());
                saveBitmap(bmp, "sssssssssssssss");
                Log.d("eric", "saveBitmap");
                count++;

                bos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存方法
     */
    public void saveBitmap(Bitmap bmp, String picName) {
        Log.e("eric", "保存图片");
        File f = getOutputMediaFile(1);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Log.i("eric", "已经保存");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("eric", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == 1) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + count + ".jpg");
        } else if (type == 2) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else if (type == 3) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "AVD_" + count + ".h264");
        } else if (type == 4) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "YUV_" + count + ".yuv");
        } else {
            Log.d("eric", "null : ");
            return null;
        }

        Log.d("eric", "path : " + mediaFile.getAbsolutePath());
        return mediaFile;
    }
}
