package com.zyp.androidaudiovideostudy.gles;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import com.zyp.androidaudiovideostudy.camera.helper.CameraHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 描述：
 * 作者：@author alex
 * 创建时间：2022/4/25 3:55 下午
 */
public class OpenGLLookupHelper {

    private int mWidth;
    private int mHeight;
    private SurfaceTexture mSurfaceTexture;
    private float[] mtx = new float[16];
    private Shader mShaderCamera = new Shader();
    private Shader mShaderLookup = new Shader();
    private Shader mShaderSign = new Shader();
    private Shader mShaderScreen = new Shader();
    private String vertex_camera = "attribute vec4 vPosition;\n" +
            "    attribute vec4 vCoord;\n" +
            "    uniform mat4 vMatrix;\n" +
            "    varying vec2 aCoord;\n" +
            "    void main(){\n" +
            "        gl_Position = vPosition; \n" +
            "        //\n" +
            "        aCoord = (vMatrix * vCoord).xy;\n" +
            "    }";
    private String frag_camera = "#extension GL_OES_EGL_image_external:require\n" +
            "    precision mediump float;\n" +
            "    varying vec2 aCoord;\n" +
            "    uniform samplerExternalOES vTexture;\n" +
            "    void main() {\n" +
            "        gl_FragColor = texture2D(vTexture,aCoord);\n" +
            "    }";

    private String vertex_lookup = "attribute vec4 vPosition;\n" +
            "    attribute vec2 vCoord;\n" +
            "    varying vec2 aCoord;\n" +
            "    void main(){\n" +
            "        gl_Position = vPosition; \n" +
            "        aCoord = vCoord.xy;\n" +
            "    }";

    private String frag_lookup = "varying highp vec2 aCoord;\n" +
            "   uniform sampler2D vTexture;\n" +
            "   uniform sampler2D vTextureLookup;\n" +
            "   uniform lowp float intensity;\n" +
            "   void main() {\n" +
            "       highp vec4 textureColor = texture2D(vTexture, aCoord);\n" +
            "       highp float blueColor = textureColor.b * 63.0;\n" +
            "       highp vec2 quad1;\n" +
            "       quad1.y = floor(floor(blueColor) / 8.0);\n" +
            "       quad1.x = floor(blueColor) - (quad1.y * 8.0);\n" +
            "       highp vec2 quad2;\n" +
            "       quad2.y = floor(ceil(blueColor) / 8.0);\n" +
            "       quad2.x = ceil(blueColor) - (quad2.y * 8.0);\n" +
            "       highp vec2 texPos1;\n" +
            "       texPos1.x = (quad1.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r);\n" +
            "       texPos1.y = (quad1.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g);\n" +
            "       highp vec2 texPos2;\n" +
            "       texPos2.x = (quad2.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r);\n" +
            "       texPos2.y = (quad2.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g);\n" +
            "       lowp vec4 newColor1 = texture2D(vTextureLookup, texPos1);\n" +
            "       lowp vec4 newColor2 = texture2D(vTextureLookup, texPos2);\n" +
            "       lowp vec4 newColor = mix(newColor1, newColor2, fract(blueColor));\n" +
            "       gl_FragColor = mix(textureColor, vec4(newColor.rgb, textureColor.w), intensity);\n" +
            "   }";

    private String vertex_screen = "attribute vec4 vPosition;\n" +
            "    attribute vec2 vCoord;\n" +
            "    varying vec2 aCoord;\n" +
            "    void main(){\n" +
            "        gl_Position = vPosition; \n" +
            "        aCoord = vCoord.xy;\n" +
            "    }";

    private String frag_screen = "precision mediump float;\n" +
            "    varying vec2 aCoord;\n" +
            "    uniform sampler2D vTexture;\n" +
            "    void main() {\n" +
            "        gl_FragColor = texture2D(vTexture,aCoord);\n" +
            "    }";

    private int vPositionCamera;
    private int vCoordCamera;
    private int vMatrixCamera;
    private int vTextureCamera;

    private int vPositionLookup;
    private int vCoordLookup;
    private int vTextureLookup;
    private int vTextureLookupTable;
    private int vIntensity;

    private int vPositionSign;
    private int vCoordSign;
    private int vTextureSign;

    private int vPositionScreen;
    private int vCoordScreen;
    private int vTextureScreen;

    private FloatBuffer mGLVertexBuffer;
    private FloatBuffer mGLTextureBuffer;
    private FloatBuffer mGLTextureOriginBuffer;
    private int[] mTexture;
    private CameraHelper mCameraHelper;

    private FrameBuffer fbo;

    private Bitmap lookup;
    private int[] lookupTextures;
    private float intensity = 0.8f;
    private boolean updateLookupTexture = true;
    private int[] mSignTextures;
    public OpenGLLookupHelper(Bitmap lookup) {
        this.lookup = lookup;
    }

    public void setLookup(Bitmap lookup) {
        this.lookup = lookup;
        updateLookupTexture = true;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }


    private void initTexture() {
        if (lookupTextures == null) {
            lookupTextures = new int[1];
            GLES20.glGenTextures(1, lookupTextures, 0);
            GLES20.glBindTexture(GL_TEXTURE_2D, lookupTextures[0]);
            GLES20.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            GLES20.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        }

        if (mSignTextures == null) {
            mSignTextures = new int[1];
            GLUtil.glGenTextures(mSignTextures);
            GLES20.glBindTexture(GL_TEXTURE_2D, mSignTextures[0]);
        }
    }

    public void initVertexBuffer() {
        mGLVertexBuffer = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mGLVertexBuffer.clear();
        float[] VERTEX = {
                -1.0f, -1.0f,
                1.0f, -1.0f,
                -1.0f, 1.0f,
                1.0f, 1.0f,
        };
        mGLVertexBuffer.put(VERTEX);

        mGLTextureOriginBuffer = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mGLTextureOriginBuffer.clear();
        float[] TEXTURE_ORIGIN = {   //屏幕坐标
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f
        };
        mGLTextureOriginBuffer.put(TEXTURE_ORIGIN);

        mGLTextureBuffer = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mGLTextureBuffer.clear();
        float[] TEXTURE = {   //逆时针旋转180 然后沿Y轴镜像
                0.0f, 0.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
        };
        mGLTextureBuffer.put(TEXTURE);
    }

    public void surfaceCreate(final GLSurfaceView glSurfaceview, int rotation) {
        mTexture = new int[1];
        GLES20.glGenTextures(1, mTexture, 0); // 创建一个纹理id
        //将纹理id传入成功创建SurfaceTexture
        mSurfaceTexture = new SurfaceTexture(mTexture[0]);
        mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                glSurfaceview.requestRender();
            }
        });
        initTexture();
        initVertexBuffer();
        createProgram();
        //这个是我的camera工具类
        mCameraHelper = new CameraHelper(Camera.CameraInfo.CAMERA_FACING_BACK, rotation);
        fbo = new FrameBuffer();
    }

    public void createProgram() {
        //创建着色器程序 并且获取着色器程序中的部分属性
        mShaderCamera.creatProgram(vertex_camera, frag_camera);
        vPositionCamera = mShaderCamera.glGetAttribLocation("vPosition");
        vCoordCamera = mShaderCamera.glGetAttribLocation("vCoord");
        vMatrixCamera = mShaderCamera.glGetUniformLocation("vMatrix");
        vTextureCamera = mShaderCamera.glGetUniformLocation("vTexture");

        mShaderLookup.creatProgram(vertex_lookup, frag_lookup);
        vPositionLookup = mShaderLookup.glGetAttribLocation("vPosition");
        vCoordLookup = mShaderLookup.glGetAttribLocation("vCoord");
        vTextureLookup = mShaderLookup.glGetUniformLocation("vTexture");
        vTextureLookupTable = mShaderLookup.glGetUniformLocation("vTextureLookup");
        vIntensity = mShaderLookup.glGetUniformLocation("intensity");

        mShaderSign.creatProgram(vertex_screen, frag_screen);
        vPositionSign = mShaderSign.glGetAttribLocation("vPosition");
        vCoordSign = mShaderSign.glGetAttribLocation("vCoord");
        vTextureSign = mShaderSign.glGetUniformLocation("vTexture");

        mShaderScreen.creatProgram(vertex_screen, frag_screen);
        vPositionScreen = mShaderScreen.glGetAttribLocation("vPosition");
        vCoordScreen = mShaderScreen.glGetAttribLocation("vCoord");
        vTextureScreen = mShaderScreen.glGetUniformLocation("vTexture");

    }

    public void surfaceChanged(int width, int height) {
        mWidth = width;
        mHeight = height;
        mCameraHelper.startPreview(mSurfaceTexture, width, height); //这个是设置相机的预览画面
        fbo.setup(width, height);
    }

    public void drawFrame() {
        fbo.begin();
        //清理屏幕：可以清理成指定的颜色
        GLES20.glClearColor(0, 0, 0, 0);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        //帖纸画上去
        //开启混合模式 ： 将多张图片进行混合(贴图)
        GLES20.glEnable(GLES20.GL_BLEND);
        //设置贴图模式
        // 1：src 源图因子 ： 要画的是源  (耳朵)
        // 2: dst : 已经画好的是目标  (从其他filter来的图像)
        //画耳朵的时候  GL_ONE:就直接使用耳朵的所有像素 原本是什么样子 我就画什么样子
        // 表示用1.0减去源颜色的alpha值来作为因子
        //  耳朵不透明 (0,0 （全透明）- 1.0（不透明）) 目标图对应位置的像素就被融合掉了 不见了
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        // fbo 绘制camera数据到纹理
        mSurfaceTexture.updateTexImage();//更新纹理成为最新的数据
        mSurfaceTexture.getTransformMatrix(mtx);

        // fbo 绘制Camera数据
        drawCamera(mTexture[0]);

        // fbo 绘制Lookup
        drawLookup(fbo.getTextureId());

        // fbo 绘制时间水印
        drawTimeSign(mSignTextures[0]);

        fbo.end();

        // 绘制到屏幕
        drawScreen(fbo.getTextureId());

    }

    private void drawCamera(int textureId) {
        GLES20.glViewport(0, 0, mWidth, mHeight);

        mShaderCamera.use();

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        GLES20.glUniform1i(vTextureCamera, 0);

        //变换矩阵
        GLES20.glUniformMatrix4fv(vMatrixCamera, 1, false, mtx, 0);

        mGLVertexBuffer.position(0);
        //  函数的意义是为 Atrtribute 变量制定VBO中的数据.  //屏幕顶点坐标
        GLES20.glVertexAttribPointer(vPositionCamera, 2, GLES20.GL_FLOAT, false, 0, mGLVertexBuffer);//设置顶点数据
        GLES20.glEnableVertexAttribArray(vPositionCamera);

        mGLTextureBuffer.position(0);
        GLES20.glVertexAttribPointer(vCoordCamera, 2, GLES20.GL_FLOAT, false, 0, mGLTextureBuffer); //纹理顶点坐标
        GLES20.glEnableVertexAttribArray(vCoordCamera);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
    }

    private void drawLookup(int textureId) {
        GLES20.glViewport(0, 0, mWidth, mHeight);
        mShaderLookup.use();
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(vTextureLookup, 0);

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
            GLES20.glBindTexture(GL_TEXTURE_2D, lookupTextures[0]);
        }

        GLES20.glUniform1i(vTextureLookupTable, 1);
        GLES20.glUniform1f(vIntensity, intensity);

        //传递坐标
        mGLVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(vPositionLookup, 2, GLES20.GL_FLOAT, false, 0, mGLVertexBuffer);
        GLES20.glEnableVertexAttribArray(vPositionLookup);

        mGLTextureBuffer.position(0);
        GLES20.glVertexAttribPointer(vCoordLookup, 2, GLES20.GL_FLOAT, false, 0, mGLTextureBuffer);
        GLES20.glEnableVertexAttribArray(vCoordLookup);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    private void drawTimeSign(int textureId) {


        updateTimeBitmap();

        //设置显示窗口
        GLES20.glViewport(100,200, mBitmap.getWidth(), mBitmap.getHeight());

        //使用着色器
        mShaderSign.use();

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //表示后续的操作 就是作用于这个纹理上
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        // 将 Bitmap与纹理id 绑定起来
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);

        GLES20.glUniform1i(vTextureSign, 0);

        //传递坐标
        mGLVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(vPositionSign, 2, GLES20.GL_FLOAT, false, 0, mGLVertexBuffer);
        GLES20.glEnableVertexAttribArray(vPositionSign);

        mGLTextureOriginBuffer.position(0);
        GLES20.glVertexAttribPointer(vCoordSign, 2, GLES20.GL_FLOAT, false, 0, mGLTextureOriginBuffer);
        GLES20.glEnableVertexAttribArray(vCoordSign);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        //关闭
        GLES20.glDisable(GLES20.GL_BLEND);

        mBitmap.recycle();
    }

    private void drawScreen(int textureId) {
        GLES20.glViewport(0, 0, mWidth, mHeight);
        mShaderScreen.use();
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(vTextureScreen, 0);

        //传递坐标
        mGLVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(vPositionScreen, 2, GLES20.GL_FLOAT, false, 0, mGLVertexBuffer);
        GLES20.glEnableVertexAttribArray(vPositionScreen);

        mGLTextureBuffer.position(0);
        GLES20.glVertexAttribPointer(vCoordScreen, 2, GLES20.GL_FLOAT, false, 0, mGLTextureBuffer);
        GLES20.glEnableVertexAttribArray(vCoordScreen);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }


    private Bitmap mBitmap;
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");

    private void updateTimeBitmap() {
        String aText = sdf.format(new Date());

        float aFontSize = 60;
        Paint textPaint = new Paint();
        textPaint.setTextSize(aFontSize);
        textPaint.setFakeBoldText(false);
        textPaint.setAntiAlias(true);
        textPaint.setARGB(255, 255, 255, 255);
        // If a hinting is available on the platform you are developing, you should enable it (uncomment the line below).
        //textPaint.setHinting(Paint.HINTING_ON);
        textPaint.setSubpixelText(true);
        textPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
        textPaint.setShadowLayer(2, 2, 2, Color.BLACK);

        float realTextWidth = textPaint.measureText(aText);

        // Creates a new mutable bitmap, with 128px of width and height
        int bitmapWidth = (int) (realTextWidth + 2.0f);
        int bitmapHeight = (int) aFontSize + 2;

        mBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        mBitmap.eraseColor(Color.argb(0, 255, 0, 0));
        // Creates a new canvas that will draw into a bitmap instead of rendering into the screen
        Canvas bitmapCanvas = new Canvas(mBitmap);
        // Set start drawing position to [1, base_line_position]
        // The base_line_position may vary from one font to another but it usually is equal to 75% of font size (height).
        bitmapCanvas.drawText(aText, 1, 1.0f + aFontSize * 0.75f, textPaint);
    }
}
