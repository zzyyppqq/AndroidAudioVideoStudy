# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.18.1)

# Declares and names the project.

project("libfmod")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.



find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)


# 引入外部so fmod 供源文件编译
add_library(fmod SHARED IMPORTED)
set_target_properties(fmod PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/fmod/libs/${ANDROID_ABI}/libfmod.so)
set_target_properties(fmod PROPERTIES LINKER_LANGUAGE CXX)

# 引入外部so fmodL 供源文件编译
add_library(fmodL SHARED IMPORTED)
set_target_properties(fmodL PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/fmod/libs/${ANDROID_ABI}/libfmodL.so)
set_target_properties(fmodL PROPERTIES LINKER_LANGUAGE CXX)

include_directories(fmod/include)
include_directories(fmod)

# 添加fmod demo库
add_library( # 生成动态库的名称
        fmod-voice-lib
        # 指定是动态库SO
        SHARED
        # 编译库的源代码文件
        fmod/play_sound.cpp
        fmod/common.cpp
        fmod/common_platform.cpp)
# 连接fmod demo库
target_link_libraries( # 指定目标链接库
        fmod-voice-lib
        # 添加预编译库到目标链接库中
        ${log-lib}
        fmod
        fmodL)

# 添加fmod effect特效库
add_library( # 生成动态库的名称
        fmod-effect-lib
        # 指定是动态库SO
        SHARED
        # 编译库的源代码文件
        fmod/effect_sound.cpp)

# 连接fmod effect特效库
target_link_libraries( # 指定目标链接库
        fmod-effect-lib
        # 添加预编译库到目标链接库中
        ${log-lib}
        fmod
        fmodL)


add_library( # Sets the name of the library.
        fmod-lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        fmod.cpp)


# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        fmod-lib
        fmod
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})