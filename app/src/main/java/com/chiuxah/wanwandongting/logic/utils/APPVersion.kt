package com.chiuxah.wanwandongting.logic.utils

import android.content.pm.PackageManager
import com.chiuxah.wanwandongting.MyApplication

object APPVersion {
    fun getVersionCode() : Int {
        var versionCode = 0
        try {
            versionCode = MyApplication.context.packageManager.getPackageInfo(MyApplication.context.packageName,0).versionCode
        } catch ( e : PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return versionCode
    }

    fun getVersionName() : String {
        var versionName = ""
        try {
            versionName = MyApplication.context.packageManager.getPackageInfo(MyApplication.context.packageName,0).versionName
        } catch ( e : PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return versionName
    }
}