//
// Created by c2yu on 2021/5/13.
//

#include "libyuv.h"
#include "yuv_convert.h"

void i420_to_rgba(char *src, char *dst, int width, int height) {
    int src_y_size = width * height;
    int src_u_size = src_y_size >> 2;
    char *src_y = src;
    char *src_u = src + src_y_size;
    char *src_v = src + src_y_size + src_u_size;

    char *dst_rgba = dst;

    I420ToRGBA((unsigned char *) src_y, width,
               (unsigned char *) src_u, width >> 1,
               (unsigned char *) src_v, width >> 1,
               (unsigned char *) dst_rgba, width * 4,
               width, height);
}

void i420_to_rgb24(char *src, char *dst, int width, int height) {
    int src_y_size = width * height;
    int src_u_size = src_y_size >> 2;
    char *src_y = src;
    char *src_u = src + src_y_size;
    char *src_v = src + src_y_size + src_u_size;

    char *dst_rgb24 = dst;

    I420ToRGB24((unsigned char *) src_y, width,
               (unsigned char *) src_u, width >> 1,
               (unsigned char *) src_v, width >> 1,
               (unsigned char *) dst_rgb24, width * 3,
               width, height);
}

void i420_to_abgr(char *src, char *dst, int width, int height) {
    int src_y_size = width * height;
    int src_u_size = src_y_size >> 2;
    char *src_y = src;
    char *src_u = src + src_y_size;
    char *src_v = src + src_y_size + src_u_size;

    char *dst_rgba = dst;
    I420ToABGR((unsigned char *) src_y, width,
               (unsigned char *) src_u, width >> 1,
               (unsigned char *) src_v, width >> 1,
               (unsigned char *) dst_rgba, width * 4,
               width, height);
}


void i420_to_nv21(char *src, char *dst, int width, int height) {
    int src_y_size = width * height;
    int src_u_size = src_y_size >> 2;
    char *src_y = src;
    char *src_u = src + src_y_size;
    char *src_v = src + src_y_size + src_u_size;

    int dst_y_size = width * height;
    char *dst_y = dst;
    char *dst_vu = dst + dst_y_size;

    I420ToNV21((unsigned char *) src_y, width,
               (unsigned char *) src_u, width >> 1,
               (unsigned char *) src_v, width >> 1,
               (unsigned char *) dst_y, width,
               (unsigned char *) dst_vu, width,
               width, height);
}


void nv21_to_i420(char *src, char *dst, int width, int height) {
    int src_y_size = width * height;
    char *src_y = src;
    char *src_vu = src + src_y_size;

    int dst_y_size = width * height;
    int dst_u_size = dst_y_size >> 2;
    char *dst_y = dst;
    char *dst_u = dst + dst_y_size;
    char *dst_v = dst + dst_y_size + dst_u_size;


    NV21ToI420((unsigned char *) src_y, width,
               (unsigned char *) src_vu, width,
               (unsigned char *) dst_y, width,
               (unsigned char *) dst_u, width >> 1,
               (unsigned char *) dst_v, width >> 1,
               width, height);
}


void rgb24_to_i420(char *src, char *dst, int width, int height){
    char * src_rgb24 = src;

    int dst_y_size = width * height;
    int dst_u_size = dst_y_size >> 2;
    char *dst_y = dst;
    char *dst_u = dst + dst_y_size;
    char *dst_v = dst + dst_y_size + dst_u_size;


    RGB24ToI420((unsigned char *)src_rgb24, width *3,
                (unsigned char *) dst_y, width,
                (unsigned char *) dst_u, width >> 1,
                (unsigned char *) dst_v, width >> 1,
                width, height);
}

int argb_to_i420(const uint8* src_frame, int src_stride_frame,
                 uint8* dst_y, int dst_stride_y,
                 uint8* dst_u, int dst_stride_u,
                 uint8* dst_v, int dst_stride_v,
                 int width, int height) {
    return ABGRToI420(src_frame, src_stride_frame,
                      dst_y, dst_stride_y,
                      dst_u, dst_stride_u,
                      dst_v, dst_stride_v,
                      width, height);
}