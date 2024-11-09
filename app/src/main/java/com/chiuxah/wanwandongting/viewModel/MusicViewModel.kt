package com.chiuxah.wanwandongting.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.chiuxah.wanwandongting.logic.dataModel.SongsInfo

class MusicViewModel : ViewModel() {
    // 是否正在播放
    val isPlaying = MutableLiveData<Boolean>(false)

    // 当前播放的歌曲 URL
    val currentSongUrl = MutableLiveData<String?>(null)

    val songInfo = MutableLiveData<SongsInfo>(SongsInfo("","","","",""))

    val songmid = MutableLiveData<String?>(null)
    // 当前播放进度（可根据需要添加其他状态）
    val currentProgress = MutableLiveData<Int>(0)

    // 更新播放状态的方法
    fun updatePlayState(isPlaying: Boolean, songUrl: String?) {
        this.isPlaying.value = isPlaying
        this.currentSongUrl.value = songUrl
    }

    // 更新播放进度
    fun updateProgress(progress: Int) {
        currentProgress.value = progress
    }
}
