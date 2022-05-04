# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.18.1)

# Declares and names the project.

project("librtmp")

set(JNI_LIBS_DIR src/main/cpp)

#打印LOG
message(WARNING "Cmake build type is: "${CMAKE_BUILD_TYPE})
message(WARNING "Cmake build android abi is: "${ANDROID_ABI})
message(WARNING "Cmake source dir is: "${CMAKE_SOURCE_DIR})
message(WARNING "Cmake arch abi is: "${CMAKE_ANDROID_ARCH_ABI})



add_library(
        rtmp
        SHARED
        IMPORTED
)

SET_TARGET_PROPERTIES(
        rtmp
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/${JNI_LIBS_DIR}/${CMAKE_ANDROID_ARCH_ABI}/librtmp.so
)

include_directories(src/main/cpp/librtmp)


# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        rtmp-lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        src/main/cpp/rtmp_lib.cpp src/main/cpp/rtmp_handle.cpp src/main/cpp/rtmp_core.c src/main/cpp/safe_queue.c)

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
        rtmp-lib
        rtmp
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})