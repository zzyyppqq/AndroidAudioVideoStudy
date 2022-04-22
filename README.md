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


