package com.chiuxah.wanwandongting.logic.network.api

import retrofit2.Call
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface QMXService {
    //获取songmid
    @GET("/getSongmid")
    fun getSongmid(
        @Query("songid") songId : String
    ) : Call<ResponseBody>

    //获取图片
    @GET("/getAlbumPicture")
    fun getAlbumPicture(
        @Query("id") id : String
    ) : Call<ResponseBody>

    //获取歌曲URL
    @GET("/getSongUrl")
    fun getSong(
        @Query("songmid") songmid : String
    ) : Call<ResponseBody>

    //获取歌词
    @GET("/getLyrics")
    fun getLyrics(
        @Query("songmid") songmid : String
    ) : Call<ResponseBody>

    //获取歌单
    @GET("/getListInfo")
    fun getListInfo(
        @Query("listId") listId : String,
        @Query("begin") begin : Int = 0,
        @Query("num") pageNum : Int = 5000
    ) : Call<ResponseBody>
}