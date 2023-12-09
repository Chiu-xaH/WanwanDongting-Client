package com.cxh.qqmusictp.viewModel

import androidx.lifecycle.ViewModel
import com.cxh.qqmusictp.logic.Prefs
import com.cxh.qqmusictp.logic.network.api.MusicService
import com.cxh.qqmusictp.logic.network.serviceCreator.MusicServiceCreator
import com.cxh.qqmusictp.logic.network.serviceCreator.ResultServiceCreator
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class MusicViewModel : ViewModel() {
    private val api = MusicServiceCreator.create(MusicService::class.java)
    private val api_result = ResultServiceCreator.create(MusicService::class.java)

    fun searchMusic(info : String) {
        val call = api.searchMusic(info)
        call.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val callback = response.body()?.string()
                val json = callback?.substring(9,callback.length-1)
                Prefs.Save("searchJson",json)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun getSongmid(songid : String) {
        val call = api.getSongmid(songid)
        call.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun getfilename(Songmid : String) {
        val call = api.getFilename(Songmid)
        call.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun getResult(filename : String) {
        val call = api_result.getResult(filename)
        call.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun getLyric(songmid : String) {
        val call = api.getLyric(songmid)
                call.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

}