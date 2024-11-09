package com.chiuxah.wanwandongting.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chiuxah.wanwandongting.logic.network.NetWork

class MyViewModel : ViewModel() {

    var searchResponse = MutableLiveData<String?>()
    fun searchSongs(query : String,num : Int = 20) {
        val call = NetWork.qmApi.searchSongs(text = query, num = num)
        NetWork.makeRequest(call,searchResponse)
    }

    var SongmidResponse = MutableLiveData<String?>()
    fun getSongmid(songId : String) {
        val call = NetWork.qmxApi.getSongmid(songId)
        NetWork.makeRequest(call,SongmidResponse)
    }

    var songLyricsResponse = MutableLiveData<String?>()
    fun getSongLyrics(songmid : String) {
        val call = NetWork.qmxApi.getLyrics(songmid)
        NetWork.makeRequest(call,songLyricsResponse)
    }

    var songUrlResponse = MutableLiveData<String?>()
    fun getSongUrl(songmid : String) {
        val call = NetWork.qmxApi.getSong(songmid)
        NetWork.makeRequest(call,songUrlResponse)
    }
}