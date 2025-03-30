package com.chiuxah.wanwandongting.logic.utils

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import com.chiuxah.wanwandongting.MyApplication
import com.chiuxah.wanwandongting.ui.utils.components.MyToast


object Starter {
    //通过包名启动第三方应用
    @SuppressLint("QueryPermissionsNeeded")
    fun startLaunchAPK(packageName: String,appName : String = "应用") {
        try {
            val intent = MyApplication.context.packageManager.getLaunchIntentForPackage(packageName)
            if(intent == null) MyToast("未安装${appName}")
            else  MyApplication.context.startActivity(intent)
        } catch (_: Exception) {
            MyToast("启动外部应用失败")
        }
    }
    //传入应用URL打开
    fun startAppUrl(url : String) {
        try {
            val intent = Intent(Intent.ACTION_DEFAULT, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            MyApplication.context.startActivity(intent)
        } catch (e : Exception) {
            MyToast("打开支付宝失败")
        }
    }
    //传入网页URL打开
    fun startWebUrl(url : String) {
        try {
            val it = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            MyApplication.context.startActivity(it)
        } catch (e : Exception) {
            MyToast("启动浏览器失败")
        }
    }
    //跳转Activity
    fun startActivity(clazz: Class<*>, extras: Map<String, Any>? = null) {
        val intent = Intent(MyApplication.context, clazz).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            extras?.forEach { (key, value) ->
                when (value) {
                    is Boolean -> putExtra(key, value)
                    is Int -> putExtra(key, value)
                    is String -> putExtra(key, value)
                    // 你可以根据需要添加更多类型
                }
            }
        }
        MyApplication.context.startActivity(intent)
    }

    fun emailMe() {
        val it = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:zsh0908@outlook.com"))
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        MyApplication.context.startActivity(it)
    }


}
