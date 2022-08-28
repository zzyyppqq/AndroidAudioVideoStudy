package com.zyp.av.yuv.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.Date;

public class YuvImageUtil {
    public static final String TAG = "YuvToImage";
    private int count = 0;

    public void image(final byte[] data, final int width,int height) {

        try {
            YuvImage image = new YuvImage(data, ImageFormat.NV21, width, height, null);
            Bitmap bmp = null;
            Log.d(TAG, "count : " + count);
            if (count <= 10 && image != null) {
                Log.d(TAG, "enabled");
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                image.compressToJpeg(new Rect(0, 0, width, height), 80, bos);
                byte[] bytes = bos.toByteArray();
                Log.d(TAG, "bytes.length: " + bytes.length + ", bos.size(): " +  bos.size());
                // decodeByteArray易OOM
                // bmp = BitmapFactory.decodeByteArray(bytes, 0, bos.size());
                BitmapFactory.Options options=new BitmapFactory.Options();
                options.inSampleSize = 8;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                ByteArrayInputStream input = new ByteArrayInputStream(bytes);
                Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
                SoftReference softRef = new SoftReference(bitmap);
                bmp = (Bitmap)softRef.get();
                saveBitmap(bmp, "sssssssssssssss");
                Log.d(TAG, "saveBitmap");
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
        Log.e(TAG, "保存图片");
        File f = getOutputMediaFile(1);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Log.i(TAG, "已经保存");
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
                Log.d(TAG, "failed to create directory");
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
            Log.d(TAG, "null : ");
            return null;
        }

        Log.d(TAG, "path : " + mediaFile.getAbsolutePath());
        return mediaFile;
    }
}
