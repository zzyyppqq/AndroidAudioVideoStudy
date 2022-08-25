# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.18.1)

# Declares and names the project.

project("libassimp")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

message("CMAKE_SOURCE_DIR: ${CMAKE_SOURCE_DIR}")
message("CMAKE_ANDROID_ARCH_ABI: ${CMAKE_ANDROID_ARCH_ABI}")

set(${CMAKE_C_FLAGS}, "${CMAKE_C_FLAGS}")
add_library(native_app_glue STATIC
        ${ANDROID_NDK}/sources/android/native_app_glue/android_native_app_glue.c)

# now build app's shared lib
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11 -Wall -Werror")

# Export ANativeActivity_onCreate(),
# Refer to: https://github.com/android-ndk/ndk/issues/381.
set(CMAKE_SHARED_LINKER_FLAGS
        "${CMAKE_SHARED_LINKER_FLAGS} -u ANativeActivity_onCreate")


add_library(
        assimp
        SHARED
        IMPORTED
)

SET_TARGET_PROPERTIES(
        assimp
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/assimp/libs/${CMAKE_ANDROID_ARCH_ABI}/libassimp.so
)

include_directories(assimp/include)

add_library(
        opencv
        SHARED
        IMPORTED
)

SET_TARGET_PROPERTIES(
        opencv
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/opencv/libs/${CMAKE_ANDROID_ARCH_ABI}/libopencv_java4.so
)

include_directories(opencv/include)

include_directories(glm)
include_directories(.)

#查找当前目录下所有源文件
#并将文件名保存在DIR_SOURECE
aux_source_directory(glm GLM_SOURCE)
message("GLM_SOURCE: ${GLM_SOURCE}")
#file(GLOB GLM_FILES glm/*.*)
# 递归查找
file(GLOB_RECURSE GLM_FILES glm/*.*)
message("GLM_FILES: ${GLM_FILES}")

file(GLOB SOURE_SRC
        jniCalls/*.cpp
        nativeCode/common/*.h
        nativeCode/common/*.cpp
        nativeCode/modelAssimp/*.h
        nativeCode/modelAssimp/*.cpp
        )
# 排除可执行文件
#list(REMOVE_ITEM SOURE_SRC "${CMAKE_SOURCE_DIR}/assimp_lib.cpp")

include_directories(jniCalls nativeCode/common nativeCode/modelAssimp)

add_library( # Sets the name of the library.
        assimp-lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${GLM_FILES}
        ${SOURE_SRC}
        assimp_lib.cpp)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

#target_include_directories(assimp-lib PRIVATE
#        ${ANDROID_NDK}/sources/android/native_app_glue)

target_link_libraries( # Specifies the target library.
        assimp-lib
        assimp
        opencv
        android
#        native_app_glue
        EGL
        GLESv1_CM
        GLESv3
        stdc++
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})