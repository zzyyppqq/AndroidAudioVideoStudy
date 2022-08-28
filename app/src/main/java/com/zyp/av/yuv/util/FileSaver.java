package com.zyp.av.yuv.util;

import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileSaver {

    // 创建FileOutputStream对象
    private FileOutputStream outputStream = null;
    // 创建BufferedOutputStream对象
    private BufferedOutputStream bufferedOutputStream = null;
    private File mFile;
    private String mFileName = "";

    public void append(byte[] bytes, String fileName) {
        try {
            if (mFile == null || !mFileName.equals(fileName)) {
                mFile = new File(Environment.getExternalStorageDirectory(), fileName);
                this.mFileName = fileName;
            }

            if (!mFile.exists()) {
                // 在文件系统中根据路径创建一个新的空文件
                mFile.createNewFile();
            }
            if (bufferedOutputStream == null) {
                // 获取FileOutputStream对象
                outputStream = new FileOutputStream(mFile, true);
                // 获取BufferedOutputStream对象
                bufferedOutputStream = new BufferedOutputStream(outputStream);
            }
            // 往文件所在的缓冲输出流中写byte数据
            bufferedOutputStream.write(bytes);
            // 刷出缓冲输出流，该步很关键，要是不执行flush()方法，那么文件的内容是空的。
            bufferedOutputStream.flush();
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
            close();
        } finally {

        }
    }

    public void close() {
        // 关闭创建的流对象
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (bufferedOutputStream != null) {
            try {
                bufferedOutputStream.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

}
