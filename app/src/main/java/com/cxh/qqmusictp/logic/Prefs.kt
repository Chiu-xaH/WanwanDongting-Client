package com.cxh.qqmusictp.logic

import android.content.Context
import android.preference.PreferenceManager
import com.cxh.qqmusictp.MyApplication

object Prefs {
    val prefs = MyApplication.context.getSharedPreferences("com.cxh.qqmusictp_preferences", Context.MODE_PRIVATE)

    fun Save(title : String,info : String?) {
        val saved = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
        if (saved.getString(title, "") != info) { saved.edit().putString(title,info).apply() }
    }

}