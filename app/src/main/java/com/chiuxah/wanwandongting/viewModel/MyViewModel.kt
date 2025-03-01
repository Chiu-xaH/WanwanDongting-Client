package com.chiuxah.wanwandongting.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chiuxah.wanwandongting.logic.network.NetWork

class MyViewModel : ViewModel() {

    var searchResponse = MutableLiveData<String?>()
    fun searchSongs(query : String,num : Int = 20,page: Int = 1) {
        val call = NetWork.qmApi.searchSongs(text = query, num = num,page = page)
        NetWork.makeRequest(call,searchResponse)
    }

    var songmidResponse = MutableLiveData<String?>()
    fun getSongmid(songId : String) {
        val call = NetWork.qmxApi.getSongmid(songId)
        NetWork.makeRequest(call,songmidResponse)
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

    var songListResponse = MutableLiveData<String?>()
    fun getListInfo(listId : String) {
        val call = NetWork.qmxApi.getListInfo(listId)
        NetWork.makeRequest(call,songListResponse)
    }
}