package com.chiuxah.wanwandongting.ui.utils.components

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.chiuxah.wanwandongting.MyApplication

fun MyToast(text : String) {
    Handler(Looper.getMainLooper()).post{ Toast.makeText(MyApplication.context,text,Toast.LENGTH_SHORT).show() }
}