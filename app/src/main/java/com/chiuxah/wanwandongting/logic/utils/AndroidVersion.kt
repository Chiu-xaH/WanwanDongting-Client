package com.chiuxah.wanwandongting.logic.utils

object AndroidVersion {
    // 获取当前系统的API级别
    val sdkInt = android.os.Build.VERSION.SDK_INT
    // 获取当前系统的版本号
    val release = android.os.Build.VERSION.RELEASE

    val isSupportedBlur = sdkInt >= 33
}