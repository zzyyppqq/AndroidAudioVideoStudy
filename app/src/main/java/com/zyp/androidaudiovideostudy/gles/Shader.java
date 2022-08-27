package com.zyp.androidaudiovideostudy.gles;

import android.opengl.GLES20;

public class Shader {

    private int mProgram = 0;

    //创建着色器程序 返回着色器id
    public void creatProgram(String vsi, String fsi) {
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
        mProgram = GLES20.glCreateProgram();
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

    }

    public int glGetAttribLocation(String name) {
        return GLES20.glGetAttribLocation(mProgram, name);
    }

    public int glGetUniformLocation(String name) {
        return GLES20.glGetUniformLocation(mProgram, name);
    }

    public void use() {
        GLES20.glUseProgram(mProgram);
    }

    public void deleteProgram() {
        GLES20.glDeleteProgram(mProgram);
    }

}
