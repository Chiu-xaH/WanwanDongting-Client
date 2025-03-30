package com.chiuxah.wanwandongting

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.compose.ui.unit.dp

class MyApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        const val qmxApi = "https://qmx-api.vercel.app/"
//        const val qmxApi = "http://115.120.209.236:5000/"
        const val qmApi = "https://c.y.qq.com/"
        const val animationSpeed = 400
        val blur = 20.dp
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}