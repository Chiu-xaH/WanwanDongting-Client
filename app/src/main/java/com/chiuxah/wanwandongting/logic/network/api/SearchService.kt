package com.chiuxah.wanwandongting.logic.network.api

import retrofit2.Call
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {
    //检索歌曲
    @GET("soso/fcgi-bin/music_search_new_platform")
    fun searchSongs(
        @Query("format") format : String = "json",
        @Query("w") text : String,
        @Query("n") num : Int = 20,
        @Query("p") page : Int = 1,
    ) : Call<ResponseBody>
}