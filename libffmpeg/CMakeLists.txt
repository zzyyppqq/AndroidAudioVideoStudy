# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.18.1)

# 定义变量
set(distribution_DIR ../../../../libs)

#打印LOG
message(STATUS "Cmake build type is: "${CMAKE_BUILD_TYPE})
message(STATUS "Cmake build android abi is: "${ANDROID_ABI})
message(STATUS "Cmake source dir is: "${CMAKE_SOURCE_DIR})

# Declares and names the project.

project("ffmpeglib")



# 添加库——自己编写的库
# 库名称：native-armeabi-v7a
# 库类型：SHARED，表示动态库，后缀为.so（如果是STATIC，则表示静态库，后缀为.a）
# 库源码文件：src/main/cpp/ffmpeglib.cpp
add_library( # Sets the name of the library.
        ffmpeglib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        libs/include/cmdutils.c
        libs/include/cmdutils.h
        libs/include/config.h
        libs/include/ffmpeg.c
        libs/include/ffmpeg.h
        libs/include/ffmpeg_filter.c
        libs/include/ffmpeg_hw.c
        libs/include/ffmpeg_opt.c
        src/main/cpp/ffmpeglib.cpp)


# 添加库——外部引入的库
# 库名称：avcodec（不需要包含前缀lib）
# 库类型：SHARED，表示动态库，后缀为.so（如果是STATIC，则表示静态库，后缀为.a）
# IMPORTED表明是外部引入的库
add_library(avcodec
        SHARED
        IMPORTED)

# 设置目标属性
# 设置avcodec目标库的IMPORTED_LOCATION属性，用于说明引入库的位置
# 还可以设置其他属性，格式：PROPERTIES key value
set_target_properties(avcodec
        PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/${ANDROID_ABI}/libavcodec.so)

add_library(avfilter
        SHARED
        IMPORTED)
set_target_properties(avfilter
        PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/${ANDROID_ABI}/libavfilter.so)

add_library(avformat
        SHARED
        IMPORTED)
set_target_properties(avformat
        PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/${ANDROID_ABI}/libavformat.so)

add_library(avutil
        SHARED
        IMPORTED)
set_target_properties(avutil
        PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/${ANDROID_ABI}/libavutil.so)

add_library(swresample
        SHARED
        IMPORTED)
set_target_properties(swresample
        PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/${ANDROID_ABI}/libswresample.so)

add_library(swscale
        SHARED
        IMPORTED)
set_target_properties(swscale
        PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/${ANDROID_ABI}/libswscale.so)

add_library(postproc
        SHARED
        IMPORTED)
set_target_properties(postproc
        PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/${ANDROID_ABI}/libpostproc.so)


add_library(avresample
        SHARED
        IMPORTED)
set_target_properties(avresample
        PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/${ANDROID_ABI}/libavresample.so)


add_library(avdevice
        SHARED
        IMPORTED)
set_target_properties(avdevice
        PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/${ANDROID_ABI}/libavdevice.so)


find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)


# 引入头文件
include_directories(libs/include)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries(
        ffmpeglib
        avcodec
        avfilter
        avformat
        avutil
        swresample
        swscale
        postproc
        avdevice
        avresample
        -landroid
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})