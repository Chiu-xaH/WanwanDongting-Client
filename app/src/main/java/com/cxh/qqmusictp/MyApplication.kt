package com.cxh.qqmusictp

import android.app.Application
import android.content.Context

class MyApplication : Application() {
    companion object {
        lateinit var context : Context
        const val URL = "https://c.y.qq.com/"
        const val ImgURL = "http://imgcache.qq.com/music/photo/album_300/"
        const val ResultURL = "http://ws.stream.qqmusic.qq.com/"
        const val Null = "{\"data\":{\"song\":{\"curnum\":10,\"curpage\":1,\"list\":[{ \"f\":\"0|0|0|0|0|0|0|0\" }]}}}"
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}