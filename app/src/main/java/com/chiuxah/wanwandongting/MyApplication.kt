package com.chiuxah.wanwandongting

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.compose.ui.unit.dp

class MyApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        const val qmxApi = "http://8.154.28.108/"
        const val qmApi = "https://c.y.qq.com/"
        const val animationSpeed = 400
        val blur = 20.dp
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}