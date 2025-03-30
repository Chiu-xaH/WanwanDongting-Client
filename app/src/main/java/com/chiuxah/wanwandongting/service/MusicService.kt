package com.chiuxah.wanwandongting.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.chiuxah.wanwandongting.MyApplication
import com.chiuxah.wanwandongting.R
import com.chiuxah.wanwandongting.logic.bean.SingleSongInfo
import com.chiuxah.wanwandongting.ui.utils.components.MyToast

class MusicService() : Service() {

    companion object {
        var mediaPlayer: MediaPlayer? = null
    }

    private val binder = MusicBinder()
    private val handler = Handler(Looper.getMainLooper())
    private var updateRunnable: Runnable? = null

    private val playlist = mutableListOf<SingleSongInfo>() // 存储歌曲
    private var currentIndex = -1 // 当前播放索引


    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    fun getQueue() : List<SingleSongInfo> {
        return playlist
    }
    fun getCurrentIndex() : Int {
        return currentIndex
    }
    fun setCurrentIndex(index : Int)  {
         this.currentIndex = index
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
                try {
                    setDataSource(url)
                    prepare()
                    start()
                } catch (_:Exception)  {
                    MyToast(MyApplication.context.getString(R.string.play_false))
                }
            }
        } else {
            // 如果 mediaPlayer 存在且处于暂停状态，则继续播放
            if (!mediaPlayer!!.isPlaying) {
                mediaPlayer?.start()
            } else {
                // 如果正在播放其他歌曲，重置并播放新的歌曲
                mediaPlayer?.reset()
                mediaPlayer?.setDataSource(url)
                mediaPlayer?.prepare()
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

    //调整进度
    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    fun addSongToPlaylist(song: SingleSongInfo) {
        playlist.add(song)
    }

    // 播放当前索引的歌曲
    private fun playCurrentSong() {
        if (playlist.size > 0 && currentIndex != -1) {
            playlist[currentIndex].url?.let { playMusic(it) }
        }
    }


    fun removeSongFromPlaylist(song: SingleSongInfo) {
        playlist.remove(song)
    }

    private fun getSong(index : Int): SingleSongInfo {
        return if(index != -1)
            playlist[index]
        else SingleSongInfo("未在播放","未在播放","","未在播放")
    }

    fun playNext() : SingleSongInfo {
        if (playlist.isNotEmpty()) {
            currentIndex = (currentIndex + 1) % playlist.size // 循环播放
            playCurrentSong()
        }
        return getSong(currentIndex)
    }

    fun playPrevious() : SingleSongInfo {
        if (playlist.isNotEmpty()) {
            currentIndex = if (currentIndex - 1 < 0) playlist.size - 1 else currentIndex - 1
            playCurrentSong()
        }
        return getSong(currentIndex)
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
