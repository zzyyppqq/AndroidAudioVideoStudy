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

int i420_to_mirror(char *src, char *dst, int width, int height) {
    int src_i420_y_size = width * height;
    int src_i420_u_size = (width >> 1) * (height >> 1);

    char *src_i420_y_data = src;
    char *src_i420_u_data = src + src_i420_y_size;
    char *src_i420_v_data = src + src_i420_y_size + src_i420_u_size;

    char *dst_i420_y_data = dst;
    char *dst_i420_u_data = dst + src_i420_y_size;
    char *dst_i420_v_data = dst + src_i420_y_size + src_i420_u_size;

    return I420Mirror((const uint8 *) src_i420_y_data, width,
                       (const uint8 *) src_i420_u_data, width >> 1,
                       (const uint8 *) src_i420_v_data, width >> 1,
                       (uint8 *) dst_i420_y_data, width,
                       (uint8 *) dst_i420_u_data, width >> 1,
                       (uint8 *) dst_i420_v_data, width >> 1,
                       width, height);
}


int i420_to_scale(char *src, int width, int height, char *dst, int dst_width,
               int dst_height, int mode) {

    int src_i420_y_size = width * height;
    int src_i420_u_size = (width >> 1) * (height >> 1);
    char *src_i420_y_data = src;
    char *src_i420_u_data = src + src_i420_y_size;
    char *src_i420_v_data = src + src_i420_y_size + src_i420_u_size;

    int dst_i420_y_size = dst_width * dst_height;
    int dst_i420_u_size = (dst_width >> 1) * (dst_height >> 1);
    char *dst_i420_y_data = dst;
    char *dst_i420_u_data = dst + dst_i420_y_size;
    char *dst_i420_v_data = dst + dst_i420_y_size + dst_i420_u_size;

    return I420Scale((const uint8 *) src_i420_y_data, width,
                      (const uint8 *) src_i420_u_data, width >> 1,
                      (const uint8 *) src_i420_v_data, width >> 1,
                      width, height,
                      (uint8 *) dst_i420_y_data, dst_width,
                      (uint8 *) dst_i420_u_data, dst_width >> 1,
                      (uint8 *) dst_i420_v_data, dst_width >> 1,
                      dst_width, dst_height,
                      mode);
}

int i420_to_crop(char* src, int src_length, int width,int height,
                 char* dst, int dst_width, int dst_height,
                 int left, int top) {
    //裁剪的区域大小不对
    if (left + dst_width > width || top + dst_height > height) {
        return -1;
    }

    //left和top必须为偶数，否则显示会有问题
    if (left % 2 != 0 || top % 2 != 0) {
        return -1;
    }

    char *src_i420_data = src;
    char *dst_i420_data = dst;

    int dst_i420_y_size = dst_width * dst_height;
    int dst_i420_u_size = (dst_width >> 1) * (dst_height >> 1);

    char *dst_i420_y_data = dst_i420_data;
    char *dst_i420_u_data = dst_i420_data + dst_i420_y_size;
    char *dst_i420_v_data = dst_i420_data + dst_i420_y_size + dst_i420_u_size;

    ConvertToI420((const uint8 *) src_i420_data, src_length,
                          (uint8 *) dst_i420_y_data, dst_width,
                          (uint8 *) dst_i420_u_data, dst_width >> 1,
                          (uint8 *) dst_i420_v_data, dst_width >> 1,
                          left, top,
                          width, height,
                          dst_width, dst_height,
                          kRotate0, FOURCC_I420);

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