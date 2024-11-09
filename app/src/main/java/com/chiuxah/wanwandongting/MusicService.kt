package com.chiuxah.wanwandongting

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper

class MusicService : Service() {

    companion object {
        var mediaPlayer: MediaPlayer? = null
    }

    private val binder = MusicBinder()
    private val handler = Handler(Looper.getMainLooper())
    private var updateRunnable: Runnable? = null

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        val url = intent?.getStringExtra("URL")

        when (action) {
            "PLAY" -> {
                if (url != null) {
                    playMusic(url)
                }
            }
            "PAUSE" -> {
                pauseMusic()
            }
            "STOP" -> {
                stopMusic()
            }
        }

        return START_STICKY
    }

    fun playMusic(url: String) {
        if (mediaPlayer == null) {
            // 如果 mediaPlayer 为空，创建并初始化
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                prepare()
                start()
            }
        } else {
            // 如果 mediaPlayer 存在且处于暂停状态，则继续播放
            if (!mediaPlayer!!.isPlaying) {
                mediaPlayer?.start()
            }
        }
        startProgressUpdate()
    }

    fun pauseMusic() {
        mediaPlayer?.pause()
        stopProgressUpdate()
    }

    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        stopProgressUpdate()
    }

    fun getDuration(): Int {
        return mediaPlayer?.duration ?: 0
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    private fun startProgressUpdate() {
        updateRunnable = object : Runnable {
            override fun run() {
                handler.postDelayed(this, 1000L)
            }
        }
        updateRunnable?.let { handler.post(it) }
    }

    private fun stopProgressUpdate() {
        updateRunnable?.let { handler.removeCallbacks(it) }
        updateRunnable = null
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
