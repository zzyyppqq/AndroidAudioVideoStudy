# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

if (${ANDROID_ABI} STREQUAL "armeabi-v7a")
    include_directories(${ANDROID_SYSROOT}/usr/include/arm-linux-androideabi)
    set(CMAKE_C_FLAGS "-DHAVE_CONFIG_H -DFPM_ARM -ffast-math -O3")
elseif (${ANDROID_ABI} STREQUAL "arm64-v8a")
    include_directories(${ANDROID_SYSROOT}/usr/include/aarch64-linux-android)
    set(CMAKE_C_FLAGS "-DHAVE_CONFIG_H -DFPM_64BIT -ffast-math -O3")
elseif (${ANDROID_ABI} STREQUAL "x86")
    include_directories(${ANDROID_SYSROOT}/usr/include/i686-linux-android)
    set(CMAKE_C_FLAGS "-DHAVE_CONFIG_H -DFPM_INTEL -ffast-math -O3")
elseif (${ANDROID_ABI} STREQUAL "x86_64")
    include_directories(${ANDROID_SYSROOT}/usr/include/x86_64-linux-android)
    set(CMAKE_C_FLAGS "-DHAVE_CONFIG_H -DFPM_64BIT -ffast-math -O3")
endif()

set(SRC_DIR src/main/cpp/external-libmad)

include_directories(src/main/cpp/external-libmad)
aux_source_directory(src/main/cpp/external-libmad SRC_LIST_MAD)

add_definitions("-DSTDC_HEADERS")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        mad-lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        src/main/cpp/NativeMP3Decoder.c src/main/cpp/FileSystem.c ${SRC_LIST_MAD})

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

target_link_libraries( # Specifies the target library.
        mad-lib

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})