# AndroidAudioVideoStudy

AudioManager audioManager;
MediaPlayer mediaPlayer;//播放视频、音频，适合播放长语音
SoundPool soundPool;//播放短的反应速度要求高的声音，比如游戏爆破音，使用独立线程载入音乐文件
MediaRecorder mediaRecorder;//录制amr、wav、mp3并保存到文件，录制mp4视频
AudioRecord audioRecord;//录制pcm
AudioTrack audioTrack;//播放pcm
//Ringtone和RingtoneManager播放铃声
//JetPlay播放音频，用于控制游戏声音特效
//AudioEffect音效控制
//TextToSpeech语音识别技术
//Vibrator震动
//AlarmManage闹钟

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

https://www.jianshu.com/p/e4058e6a45a5

https://www.jianshu.com/p/8da3cf058c0f

音频编码库（pcm->mp3）：lame
音频解码库（mp3->pcm）：git clone http://gitorious.org/rowboat/external-libmad.git

PCM: 一种音频格式，能够到底最高保真水平的。因此，PCM约定俗成了无损编码，

LAME: 目前最好的MP3编码引擎,所谓编码，即把未压缩的音乐压缩为mp3。由于AMR已经压缩的格式，所以不能直接使用LAME转为MP3。

FFmpeg： 一套可以用来记录、转换数字音频、视频，并能将其转化为流的开源计算机程序。我们可以使用FFmpeg解码AMR，将AMR转为PCM。
https://www.jianshu.com/p/dca127703886



**Native Crash**

导出非root手机的/data/tombstones文件
adb bugreport ~/

导出anr
adb pull /data/anr/trace.txt .

- 线上通过 Bugly 框架实时监控程序异常状况
- 线下局域网使用 Google 开源的 breakpad 框架
- 爱奇艺xcrash
- 发生异常就搜集日志上传服务器(这里要注意的是日志上传的性能问题，后面省电模块会说明)
- [so 动态库崩溃问题定位（addr2line与objdump）](https://www.cnblogs.com/yipianchuyun/p/13130155.html)
  - addr2line 工具（它是标准的 GNU Binutils 中的一部分）是一个可以将指令的地址和可执行映像转换成文件名、函数名和源代码行数的工具。
    一般适用于 debug 版本或带有 symbol 信息的库。

    - ```
      addr2line位置：ndk-bundle/toolchains/arm-linux-androideabi-4.9/prebuilt/darwin-x86_64/bin/arm-linux-androideabi-addr2line

      使用addr2line 对地址进行转换
      // 转换地址的命令
      arm-linux-androideabi-addr2line -C -f -e ${SOPATH} ${Address}
       -C -f  			//打印错误行数所在的函数名称
       -e    	   		//打印错误地址的对应路径及行数
       ${SOPATH}  		//so库路径 
       ${Address}		//需要转换的堆栈错误信息地址，可以添加多个，但是中间要用空格隔开

      自己编译的jni库位置：app/build/intermediates/cmake/debug/obj/assimp.libs.armeabi-v7a/xxx.so
      里面有符号

      $ arm-linux-androideabi-addr2line -C -f -e libxxx.so 000033a3(对应的地址)

      例子：
      $ arm-linux-androideabi-addr2line -C -f -e libopengl.so 0x00020dca
      ImageRender::DoDraw()
      /Users/zhangyipeng/Documents/NaLongProject/BlogDemo/app/.cxx/cmake/debug/assimp.libs.armeabi-v7a/../../../../src/main/cpp/render/image_render.cpp:34
      ```
  - objdump 是 gcc 工具，用来查看编译后目标文件的组成。

    - ```
      objdump位置：ndk-bundle/toolchains/arm-linux-androideabi-4.9/prebuilt/darwin-x86_64/bin/arm-linux-androideabi-objdump

      使用addr2line
      arm-linux-androideabi-objdump -d libxxx.so > log.txt
      ```
  - arm-linux-androideabi-nm

    - ```
      arm-linux-androideabi-nm -D libxxx.so > log.txt
      ```
  - ndk-stack

    - ndk-bundle/ndk-stack
    - ```
      ndk-stack -sym app/build/intermediates/cmake/debug/obj/assimp.libs.armeabi-v7a -dump error.log
      ```


ndk-stack -sym libfdk-aac/build/intermediates/cmake/debug/obj/assimp.libs.armeabi-v7a -dump tombstone_07


# camera风格、Assimp模型动画 参考：
AiyaEffectSDK