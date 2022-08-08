package com.zyp.androidaudiovideostudy.yuv.util;

import static com.zyp.androidaudiovideostudy.AppKt.app;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.zyp.androidaudiovideostudy.util.FileUtils;
import com.zyp.androidaudiovideostudy.util.ToastUtil;

import java.nio.ByteBuffer;

public class YUV420To888 {
    private static final String TAG = "YUV420To888";

    /**
     * @return
     */
    public static void createImage(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.i(TAG, "bitmap width: " + width + ", height: " + height);
        ImageReader imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2);
        // 注册一个监听器，当ImageReader有一个新的Image变得可用时候调用。
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Image image = reader.acquireLatestImage();
                Log.i(TAG, "onImageAvailable ImageReader image: " + image);
                byte[][] yuvBytes = convert(image);
                FileUtils.saveFile(yuvBytes[0], String.format("/sdcard/bdd_%sx%s_y.y", width, height));
                FileUtils.saveFile(yuvBytes[1], String.format("/sdcard/bdd_%sx%s_u.y", width, height));
                FileUtils.saveFile(yuvBytes[2], String.format("/sdcard/bdd_%sx%s_v.y", width, height));
                ToastUtil.INSTANCE.show("yuv success");
            }
        }, new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Log.i(TAG, "handleMessage msg: " + msg);
            }
        });
        Surface surface = imageReader.getSurface();
        Canvas canvas = surface.lockHardwareCanvas();
        Paint paint = new Paint();
        canvas.drawBitmap(bitmap, 0f, 0f, paint);
        surface.unlockCanvasAndPost(canvas);
    }

    private void saveJpeg(Image image,String name) {

        int width = image.getWidth();
        int height = image.getHeight();
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;

        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        // bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        // ImageSaveUtil.saveBitmap2file(bitmap,getApplicationContext(),name);

    }


    public static byte[][] convert(Image image) {
        final Image.Plane[] planes = image.getPlanes();
        Log.i(TAG, "planes length: " + planes.length);
        int width = image.getWidth();
        int height = image.getHeight();

        byte[] yBytes = new byte[width * height];//y分量
        int dstIndex = 0;

        byte uBytes[] = new byte[width * height / 4];//u分量
        byte vBytes[] = new byte[width * height / 4];//v分量
        int uIndex = 0;
        int vIndex = 0;

        int pixelsStride, rowStride;
        for (int i = 0; i < planes.length; i++) {
            pixelsStride = planes[i].getPixelStride();
            rowStride = planes[i].getRowStride();

            ByteBuffer buffer = planes[i].getBuffer();

            byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);

            int srcIndex = 0;
            if (i == 0) {
                for (int j = 0; j < height; j++) {
                    System.arraycopy(bytes, srcIndex, yBytes, dstIndex, width);
                    srcIndex += rowStride;
                    dstIndex += width;
                }
            } else if (i == 1) {
                for (int j = 0; j < height / 2; j++) {
                    for (int k = 0; k < width / 2; k++) {
                        uBytes[uIndex++] = bytes[srcIndex];
                        srcIndex += pixelsStride;
                    }
                    if (pixelsStride == 2) {
                        srcIndex += rowStride - width;
                    } else if (pixelsStride == 1) {
                        srcIndex += rowStride - width / 2;
                    }
                }
            } else if (i == 2) {
                for (int j = 0; j < height / 2; j++) {
                    for (int k = 0; k < width / 2; k++) {
                        vBytes[vIndex++] = bytes[srcIndex];
                        srcIndex += pixelsStride;
                    }
                    if (pixelsStride == 2) {
                        srcIndex += rowStride - width;
                    } else if (pixelsStride == 1) {
                        srcIndex += rowStride - width / 2;
                    }
                }
            }
        }
        return new byte[][]{yBytes, uBytes, vBytes};
    }

    private byte[] rgbValuesFromBitmap(Bitmap bitmap) {
        ColorMatrix colorMatrix = new ColorMatrix();
        ColorFilter colorFilter = new ColorMatrixColorFilter(
                colorMatrix);
        Bitmap argbBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(argbBitmap);

        Paint paint = new Paint();

        paint.setColorFilter(colorFilter);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int componentsPerPixel = 3;
        int totalPixels = width * height;
        int totalBytes = totalPixels * componentsPerPixel;

        byte[] rgbValues = new byte[totalBytes];
        @ColorInt int[] argbPixels = new int[totalPixels];
        argbBitmap.getPixels(argbPixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < totalPixels; i++) {
            @ColorInt int argbPixel = argbPixels[i];
            int red = Color.red(argbPixel);
            int green = Color.green(argbPixel);
            int blue = Color.blue(argbPixel);
            rgbValues[i * componentsPerPixel + 0] = (byte) red;
            rgbValues[i * componentsPerPixel + 1] = (byte) green;
            rgbValues[i * componentsPerPixel + 2] = (byte) blue;
        }

        return rgbValues;
    }

    /**
     * 通过ImageReader获得数据来源借助libyuv做转换
     * Image image = mImageReader.acquireLatestImage()
     * 图片格式决定了image里面planes有几个数组
     *
     * @param format
     * @return
     */
    public static int getNumPlanesForFormat(int format) {
        switch (format) {
            case ImageFormat.YV12:
            case ImageFormat.YUV_420_888:
            case ImageFormat.NV21:
                return 3;
            case ImageFormat.NV16:
                return 2;
            case PixelFormat.RGB_565:
            case PixelFormat.RGBA_8888:
            case PixelFormat.RGBX_8888:
            case PixelFormat.RGB_888:
            case ImageFormat.JPEG:
            case ImageFormat.YUY2:
            case ImageFormat.Y8:
                //case ImageFormat.Y16:
            case ImageFormat.RAW_SENSOR:
            case ImageFormat.RAW_PRIVATE:
            case ImageFormat.RAW10:
            case ImageFormat.RAW12:
            case ImageFormat.DEPTH16:
            case ImageFormat.DEPTH_POINT_CLOUD:
                //case ImageFormat.RAW_DEPTH:
            case ImageFormat.DEPTH_JPEG:
            case ImageFormat.HEIC:
                return 1;
            case ImageFormat.PRIVATE:
                return 0;
            default:
                throw new UnsupportedOperationException(
                        String.format("Invalid format specified %d", format));
        }
    }

}
