//
// Created by c2yu on 2021/5/13.
//

#ifndef X264CMAKE_YUV_CONVERT_H
#define X264CMAKE_YUV_CONVERT_H

#endif //X264CMAKE_YUV_CONVERT_H


void i420_to_rgba(char *src, char *dst, int width, int height);

void i420_to_rgb24(char *src, char *dst, int width, int height);

void i420_to_abgr(char *src, char *dst, int width, int height);

int i420_to_mirror(char *src, char *dst, int width, int height);

int
i420_to_scale(char *src, int width, int height, char *dst, int dst_width, int dst_height, int mode);

int i420_to_crop(char* src, int src_length, int width,int height,
                 char* dst, int dst_width, int dst_height,
                 int left, int top);

/**
 * i420 convert to nv21
 */
void i420_to_nv21(char *src, char *dst, int width, int height);

/**
 * nv21 convert to i420
 */
void nv21_to_i420(char *src, char *dst, int width, int height);


void rgb24_to_i420(char *src, char *dst, int width, int height);


int argb_to_i420(const uint8 *src_frame, int src_stride_frame,
                         uint8 *dst_y, int dst_stride_y,
                         uint8 *dst_u, int dst_stride_u,
                         uint8 *dst_v, int dst_stride_v,
                         int width, int height);