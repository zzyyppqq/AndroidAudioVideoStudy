package com.zyp.androidaudiovideostudy.gles.filter;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glBindTexture;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;

import com.zyp.androidaudiovideostudy.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


/**
 * 贴纸滤镜
 */
public class LookUpFilter extends AbstractFrameFilter {

    private Bitmap lookup;
    private float intensity = 0.8f;

    private int vTextureLookup;
    private int vIntensity;

    private boolean updateLookupTexture = true;

    public LookUpFilter(Context context, Bitmap lookup) {
        super(context, R.raw.lookup_vertex, R.raw.lookup_frag);
        this.lookup = lookup;
    }

    @Override
    protected void initilize(Context context) {
        super.initilize(context);

        vTextureLookup = GLES20.glGetUniformLocation(mGLProgramId,
                "vTextureLookup");
        vIntensity = GLES20.glGetUniformLocation(mGLProgramId,
                "intensity");
        initTexture();
    }

    private int[] lookupTextures;

    private void initTexture() {
        lookupTextures = new int[1];
        GLES20.glGenTextures(1, lookupTextures, 0);
        GLES20.glBindTexture(GL_TEXTURE_2D, lookupTextures[0]);
        GLES20.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        GLES20.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    }

    @Override
    public void onReady(int width, int height) {
        super.onReady(width, height);
    }

    public void setLookup(Bitmap lookup) {
        this.lookup = lookup;
        updateLookupTexture = true;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    @Override
    public int onDrawFrame(int textureId) {
        //设置显示窗口
        GLES20.glViewport(0, 0, mOutputWidth, mOutputHeight);

        //不调用的话就是默认的操作glsurfaceview中的纹理了。显示到屏幕上了
        //这里我们还只是把它画到fbo中(缓存)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);

        //使用着色器
        GLES20.glUseProgram(mGLProgramId);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(vTexture, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        if (updateLookupTexture) { // 更新Lookup
            int size = lookup.getRowBytes() * lookup.getHeight();
            ByteBuffer pixelBuffer = ByteBuffer.allocate(size);
            pixelBuffer.order(ByteOrder.BIG_ENDIAN);
            lookup.copyPixelsToBuffer(pixelBuffer);
            pixelBuffer.position(0);

            GLES20.glBindTexture(GL_TEXTURE_2D, lookupTextures[0]);
            GLES20.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, lookup.getWidth(), lookup.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, pixelBuffer);

            lookup.recycle();
            updateLookupTexture = false;
        } else {
            glBindTexture(GL_TEXTURE_2D, lookupTextures[0]);
        }

        GLES20.glUniform1i(vTextureLookup, 1);
        GLES20.glUniform1f(vIntensity, intensity);

        //传递坐标
        mGLVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, mGLVertexBuffer);
        GLES20.glEnableVertexAttribArray(vPosition);

        mGLTextureBuffer.position(0);
        GLES20.glVertexAttribPointer(vCoord, 2, GLES20.GL_FLOAT, false, 0, mGLTextureBuffer);
        GLES20.glEnableVertexAttribArray(vCoord);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        //返回fbo的纹理id
        return mFrameBufferTextures[0];
    }

    @Override
    public void release() {
        super.release();
        lookup.recycle();
    }
}
