# AndroidAudioVideoStudy

- 编译lame
拷贝源码目录下libmp3lame目录中的.c和.h文件到Android studio工程cpp目录下

同时拷贝include目录下的lame.h

修改如下几个文件

    删除fft.c文件的47行的 #include "vector/lame_intrin.h"

    删除set_get.h文件的24行的#include <lame.h>

    将util.h文件的574行的"extern ieee754_float32_t fast_log2(ieee754_float32_t x);" 替换为 "extern float fast_log2(float x);"

    本例中直接将lame编译成静态库，注Android.mk一定要添加 LOCAL_CFLAGS = -DSTDC_HEADERS，不然编译出错
    cmake 中添加add_definitions("-DSTDC_HEADERS");

https://www.jianshu.com/p/fb531239cd79
