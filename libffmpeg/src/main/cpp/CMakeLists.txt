# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.18.1)

# 定义变量
set(JNI_LIBS_DIR .)

#打印LOG
message(STATUS "Cmake build type is: "${CMAKE_BUILD_TYPE})
message(STATUS "Cmake build android abi is: "${ANDROID_ABI})
message(STATUS "Cmake source dir is: "${CMAKE_SOURCE_DIR})

# Declares and names the project.

project("ffmpeglib")


#  ------------------------------------ ffmpeg -------------------------------------------------

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
        ${CMAKE_SOURCE_DIR}/ffmpeg/libs/${CMAKE_ANDROID_ARCH_ABI}/libavcodec.so)

add_library(avfilter
        SHARED
        IMPORTED)
set_target_properties(avfilter
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/ffmpeg/libs/${CMAKE_ANDROID_ARCH_ABI}/libavfilter.so)

add_library(avformat
        SHARED
        IMPORTED)
set_target_properties(avformat
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/ffmpeg/libs/${CMAKE_ANDROID_ARCH_ABI}/libavformat.so)

add_library(avutil
        SHARED
        IMPORTED)
set_target_properties(avutil
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/ffmpeg/libs/${CMAKE_ANDROID_ARCH_ABI}/libavutil.so)

add_library(swresample
        SHARED
        IMPORTED)
set_target_properties(swresample
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/ffmpeg/libs/${CMAKE_ANDROID_ARCH_ABI}/libswresample.so)

add_library(swscale
        SHARED
        IMPORTED)
set_target_properties(swscale
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/ffmpeg/libs/${CMAKE_ANDROID_ARCH_ABI}/libswscale.so)

add_library(postproc
        SHARED
        IMPORTED)
set_target_properties(postproc
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/ffmpeg/libs/${CMAKE_ANDROID_ARCH_ABI}/libpostproc.so)


add_library(avresample
        SHARED
        IMPORTED)
set_target_properties(avresample
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/ffmpeg/libs/${CMAKE_ANDROID_ARCH_ABI}/libavresample.so)


add_library(avdevice
        SHARED
        IMPORTED)
set_target_properties(avdevice
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/ffmpeg/libs/${CMAKE_ANDROID_ARCH_ABI}/libavdevice.so)

#  ------------------------------------ faac -------------------------------------------------

add_library(
        faac
        STATIC
        IMPORTED
)

SET_TARGET_PROPERTIES(
        faac
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/faac/libs/${CMAKE_ANDROID_ARCH_ABI}/libfaac.so
)

include_directories(faac)
include_directories(faac/include)

#  ------------------------------------ fdk-aac -------------------------------------------------


add_library(
        fdk-aac
        STATIC
        IMPORTED
)

SET_TARGET_PROPERTIES(
        fdk-aac
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/fdk-aac/libs/${CMAKE_ANDROID_ARCH_ABI}/libfdk-aac.a
)

include_directories(fdk-aac)

#  ------------------------------------ rtmp -------------------------------------------------

add_library(
        rtmp
        SHARED
        IMPORTED
)
# 为了避免 与其他(librtmp)module的【librtmp.so】冲突，这里改 文件名为【librtmp_.so】
SET_TARGET_PROPERTIES(
        rtmp
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/rtmp/libs/${CMAKE_ANDROID_ARCH_ABI}/librtmp_.so
)

include_directories(rtmp/include)

#  ------------------------------------ x264 -------------------------------------------------

add_library(
        x264
        STATIC
        IMPORTED
)

SET_TARGET_PROPERTIES(
        x264
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/x264/libs/${CMAKE_ANDROID_ARCH_ABI}/libx264.a
)

include_directories(x264/include)

#  ------------------------------------ yuv -------------------------------------------------


add_library(
        yuv
        STATIC
        IMPORTED
)

SET_TARGET_PROPERTIES(
        yuv
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/yuv/libs/${CMAKE_ANDROID_ARCH_ABI}/libyuv.a
)

include_directories(yuv/include)

#  ------------------------------------ log -------------------------------------------------


find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)


# 引入头文件
include_directories(ffmpeg/include)
include_directories(ffmpeg)
include_directories(common)
include_directories(push)


#  ------------------------------------ library -------------------------------------------------

add_library( # Sets the name of the library.
        ffmpeglib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ffmpeg/cmdutils.c
        ffmpeg/cmdutils.h
        ffmpeg/config.h
        ffmpeg/ffmpeg.c
        ffmpeg/ffmpeg.h
        ffmpeg/ffmpeg_filter.c
        ffmpeg/ffmpeg_hw.c
        ffmpeg/ffmpeg_opt.c
        ffmpeg/ffmpeglib.cpp
        ffmpeg/NativewindRender.cpp)


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



add_library( # 生成动态库的名称
        zzr-ffmpeg-player
        # 指定是动态库SO
        SHARED
        # 编译库的源代码文件
        ffmpeg/ffmpeg_player.c)

target_link_libraries( # 指定目标链接库
        zzr-ffmpeg-player
        # 添加预编译库到目标链接库中
        ${log-lib}
        ${android-lib}
        avutil
        avcodec
        avformat
        swscale
        swresample
        -landroid
        yuv )

add_library( # 生成动态库的名称
        sync-player
        # 指定是动态库SO
        SHARED
        # 编译库的源代码文件
        ffmpeg/sync_player.c
        ffmpeg/AVPacket_buffer.c
        common/common.c)

target_link_libraries( # 指定目标链接库
        sync-player
        # 添加预编译库到目标链接库中
        ${log-lib}
        ${android-lib}
        avutil
        avcodec
        avformat
        swscale
        swresample
        -landroid
        yuv )


add_library( # 生成动态库的名称
        zzr-ffmpeg-utils
        # 指定是动态库SO
        SHARED
        # 编译库的源代码文件
        ffmpeg/ffmpeg_util.c)

target_link_libraries( # 指定目标链接库
        zzr-ffmpeg-utils
        # 添加预编译库到目标链接库中
        ${log-lib}
        avutil
        avcodec
        avformat
        swscale
        swresample )


add_library( # 生成动态库的名称
        rtmp-push
        # 指定是动态库
        SHARED
        # 编译库的源代码文件
        common/common.c
        push/queue.c
        push/rtmp_push.c )

target_link_libraries( # 指定目标链接库
        rtmp-push
        # 添加预编译库到目标链接库中
        ${log-lib}
        x264
        faac
        rtmp)