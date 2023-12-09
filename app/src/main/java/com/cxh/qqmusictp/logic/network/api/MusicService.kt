package com.cxh.qqmusictp.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface MusicService {

    //搜索音乐
    @GET("soso/fcgi-bin/music_search_new_platform")
    fun searchMusic( @Query("w") info : String ) : Call<ResponseBody>

    //获取Songmid
    @GET("v8/fcg-bin/fcg_play_single_song.fcg?tpl=yqq_song_detail&format=jsonp&callback=getOneSongInfoCallback")
    fun getSongmid(@Query("songid") songid : String) : Call<ResponseBody>

    //获取filename
    @GET("cgi-bin/musicu.fcg?format=json&data=%7B%22req_0%22%3A%7B%22module%22%3A%22vkey.GetVkeyServer%22%2C%22method%22%3A%22CgiGetVkey%22%2C%22param%22%3A%7B%22guid%22%3A%22358840384%22%2C%22songmid%22%3A%5B%22{Songmid}%22%5D%2C%22songtype%22%3A%5B0%5D%2C%22uin%22%3A%221443481947%22%2C%22loginflag%22%3A1%2C%22platform%22%3A%2220%22%7D%7D%2C%22comm%22%3A%7B%22uin%22%3A%2218585073516%22%2C%22format%22%3A%22json%22%2C%22ct%22%3A24%2C%22cv%22%3A0%7D%7D")
    fun getFilename(@Path("Songmid") Songmid : String) : Call<ResponseBody>

    //获取播放链接
    @GET("{filename}")
    fun getResult(@Path("filename") filename : String) : Call<ResponseBody>

    //获取歌词
    @GET("lyric/fcgi-bin/fcg_query_lyric_new.fcg?format=json&nobase64=1")
    @Headers("Referer:https://y.qq.com/portal/player.html")
    fun getLyric(@Query("songmid") songmid : String) :Call<ResponseBody>
}