package com.zyp.androidaudiovideostudy.gles;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.zyp.androidaudiovideostudy.util.CameraHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * 描述：
 * 作者：@author alex
 * 创建时间：2022/4/25 3:55 下午
 */
public class OpenGLHelper {

    private int mWidth;
    private int mHeight;
    private SurfaceTexture mSurfaceTexture;
    private float[] mtx = new float[16];
    private int mProgramId;
    private String vertex = "attribute vec4 vPosition;\n" +
            "    attribute vec4 vCoord;\n" +
            "    uniform mat4 vMatrix;\n" +
            "    varying vec2 aCoord;\n" +
            "    void main(){\n" +
            "        gl_Position = vPosition; \n" +
            "        //\n" +
            "        aCoord = (vMatrix * vCoord).xy;\n" +
            "    }";
    private String frag = "#extension GL_OES_EGL_image_external:require\n" +
            "    precision mediump float;\n" +
            "    varying vec2 aCoord;\n" +
            "    uniform samplerExternalOES vTexture;\n" +
            "    void main() {\n" +
            "        gl_FragColor = texture2D(vTexture,aCoord);\n" +
            "    }";

    private String frag_black_white = "" +
            //使用外部纹理必须支持此扩展
            "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "varying vec2 aCoord;\n" +
            //外部纹理采样器
            "uniform samplerExternalOES vTexture;\n" +
            "void main() \n" +
            "{\n" +
            //获取此纹理（预览图像）对应坐标的颜色值
            "  vec4 vCameraColor = texture2D(vTexture, aCoord);\n" +
            //求此颜色的灰度值
            "  float fGrayColor = (0.3*vCameraColor.r + 0.59*vCameraColor.g + 0.11*vCameraColor.b);\n" +
            //将此灰度值作为输出颜色的RGB值，这样就会变成黑白滤镜
            "  gl_FragColor = vec4(fGrayColor, fGrayColor, fGrayColor, 1.0);\n" +
            "}\n";

    private int vPosition;
    private int vCoord;
    private int vMatrix;
    private int vTexture;
    private FloatBuffer mGLVertexBuffer;
    private FloatBuffer mGLTextureBuffer;
    private int[] mTexture;
    private CameraHelper mCameraHelper;


    //创建着色器程序 返回着色器id
    private int creatProgram(String vsi, String fsi) {
        int vShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);//创建一个顶点着色器
        GLES20.glShaderSource(vShader, vsi); //加载顶点着色器代码
        GLES20.glCompileShader(vShader); //编译

        int[] status = new int[1];
        GLES20.glGetShaderiv(vShader, GLES20.GL_COMPILE_STATUS, status, 0);//获取状态
        if (status[0] != GLES20.GL_TRUE) { //判断是否创建成功
            throw new IllegalStateException("顶点着色器创建失败！");
        }

        int fShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);//创建一个顶点着色器
        GLES20.glShaderSource(fShader, fsi);//加载顶点着色器代码
        GLES20.glCompileShader(fShader);
        GLES20.glGetShaderiv(fShader, GLES20.GL_COMPILE_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            throw new IllegalStateException("片元着色器创建失败");
        }

        //创建着色器程序
        int mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vShader);//将着色器塞入程序中
        GLES20.glAttachShader(mProgram, fShader);
        GLES20.glLinkProgram(mProgram);//链接
        //获取状态，判断是否成功
        GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            throw new IllegalStateException("link program:" + GLES20.glGetProgramInfoLog(mProgram));
        }

        GLES20.glDeleteShader(vShader);
        GLES20.glDeleteShader(fShader);

        return mProgram;
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
        mGLTextureBuffer = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mGLTextureBuffer.clear();
        float[] TEXTURE = {   //屏幕坐标
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                0.0f, 1.0f,
        };
        mGLTextureBuffer.put(TEXTURE);
    }

    public enum FilterType {
        NONE,
        BLACK_WHITE
    }

    public void surfaceCreate(final GLSurfaceView glSurfaceview) {
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
        //这个是我的camera工具类
        mCameraHelper = new CameraHelper(Camera.CameraInfo.CAMERA_FACING_BACK);
        mCameraHelper.startPreview(mSurfaceTexture); //这个是设置相机的预览画面

        initVertexBuffer();
    }

    public void createProgram(FilterType filterType) {
        //创建着色器程序 并且获取着色器程序中的部分属性
        if (filterType == FilterType.BLACK_WHITE) {
            mProgramId = creatProgram(vertex, frag_black_white);
        } else {
            mProgramId = creatProgram(vertex, frag);
        }
        vPosition = GLES20.glGetAttribLocation(mProgramId, "vPosition");
        vCoord = GLES20.glGetAttribLocation(mProgramId, "vCoord");
        vMatrix = GLES20.glGetUniformLocation(mProgramId, "vMatrix");
        vTexture = GLES20.glGetUniformLocation(mProgramId, "vTexture");

    }

    public void surfaceChanged(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public void drawFrame() {
        //清理屏幕：可以清理成指定的颜色
        GLES20.glClearColor(0, 0, 0, 0);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        mSurfaceTexture.updateTexImage();//更新纹理成为最新的数据
        mSurfaceTexture.getTransformMatrix(mtx);

        GLES20.glViewport(0, 0, mWidth, mHeight);

        GLES20.glUseProgram(mProgramId);

        mGLVertexBuffer.position(0);
        //  函数的意义是为 Atrtribute 变量制定VBO中的数据.  //屏幕顶点坐标
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, mGLVertexBuffer);//设置顶点数据
        GLES20.glEnableVertexAttribArray(vPosition);

        mGLTextureBuffer.position(0);
        GLES20.glVertexAttribPointer(vCoord, 2, GLES20.GL_FLOAT, false, 0, mGLTextureBuffer); //纹理顶点坐标
        GLES20.glEnableVertexAttribArray(vCoord);

        //变换矩阵
        GLES20.glUniformMatrix4fv(vMatrix, 1, false, mtx, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTexture[0]);
        GLES20.glUniform1i(vTexture, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }
}
