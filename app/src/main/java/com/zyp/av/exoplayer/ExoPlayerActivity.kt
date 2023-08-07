package com.zyp.av.exoplayer

import android.os.Bundle
import android.os.Looper
import android.util.Log
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.drm.DrmSessionManagerProvider
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.DefaultHlsDataSourceFactory
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy
import com.google.android.exoplayer2.util.EventLogger
import com.google.common.collect.ImmutableList
import com.zyp.av.base.BaseActivity
import com.zyp.av.databinding.ActivityExoPlayerBinding


class ExoPlayerActivity : BaseActivity() {
    private val TAG = "ExoPlayerActivity"

    private var _binding: ActivityExoPlayerBinding? = null
    private val mBinding get() = _binding!!

    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityExoPlayerBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        player = ExoPlayer.Builder(this).build()

        mBinding.styledPlayerView.player = player

        player.addListener(mPlayerListener)
        player.addAnalyticsListener(mAnalyticsListener)
        player.addAnalyticsListener(EventLogger())
        //在指定播放位置触发事件
        player.createMessage(object :PlayerMessage.Target{
            override fun handleMessage(messageType: Int, message: Any?) {

            }
        }).setLooper(Looper.getMainLooper())
            .setPosition(0, 120_000)
            .setPayload(Any())
            .setDeleteAfterDelivery(false)
            .send()

        playOne()
    }

    private fun mediaSource() {
//        val dataSourceFactory = object :DataSource.Factory {
//            override fun createDataSource(): DataSource {
//
//            }
//        }
//        val mediaSourceFactory = DefaultMediaSourceFactory(this)
//            .setDataSourceFactory(dataSourceFactory)
//            //.setLocalAdInsertionComponents()
//        val player = ExoPlayer.Builder(this)
//            .setMediaSourceFactory(mediaSourceFactory)
//            .build()
////        player.setMediaSource()
    }

    private val videoUri = "https://storage.googleapis.com/exoplayer-test-media-1/mkv/android-screens-lavf-56.36.100-aac-avc-main-1280x720.mkv"

    private fun playOne() {
        val mediaItem = MediaItem.fromUri(videoUri)
        player.setMediaItem(mediaItem)

        player.prepare()
        player.play()
    }

    /**
     * 播放列表
     */
    private fun playList() {
        val firstItem = MediaItem.fromUri("https://html5demos.com/assets/dizzy.mp4")
        val secondItem = MediaItem.fromUri("https://html5demos.com/assets/dizzy.mp4")
        player.addMediaItem(firstItem)
        player.addMediaItem(secondItem)

        player.prepare()
        player.play()
    }

    /**
     * 修改播放列表
     */
    private fun modityPlayList() {
        val thirdItem = MediaItem.fromUri("https://html5demos.com/assets/dizzy.mp4")
        player.addMediaItem(1, thirdItem)
        player.moveMediaItem(2, 0)
        player.removeMediaItem(0)

        val newItems: List<MediaItem> = ImmutableList.of(
            MediaItem.fromUri("https://html5demos.com/assets/dizzy.mp4"),
            MediaItem.fromUri("https://html5demos.com/assets/dizzy.mp4")
        )
        player.setMediaItems(newItems, true)
        player.clearMediaItems()
    }

    private val mAnalyticsListener = object :AnalyticsListener {

    }

    private val mPlayerListener = object :Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            Log.i(TAG, "playbackState: $playbackState")
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            super.onPlayWhenReadyChanged(playWhenReady, reason)
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            Log.e(TAG, "onPlayerError: ${error.message}")
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
        }

        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
        }

        override fun onTracksChanged(tracks: Tracks) {
            super.onTracksChanged(tracks)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}