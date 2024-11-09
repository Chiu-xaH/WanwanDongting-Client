package com.chiuxah.wanwandongting.logic.network

import androidx.lifecycle.MutableLiveData
import com.chiuxah.wanwandongting.logic.network.api.QMXService
import com.chiuxah.wanwandongting.logic.network.api.SearchService
import com.chiuxah.wanwandongting.logic.network.serviceCreator.QMXServiceCreator
import com.chiuxah.wanwandongting.logic.network.serviceCreator.SearchServiceCreator
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await

object NetWork {
    //引入接口
    val qmxApi = QMXServiceCreator.create(QMXService::class.java)
    val qmApi = SearchServiceCreator.create(SearchService::class.java)
    // 通用的网络请求方法，支持自定义的操作
    fun <T> makeRequest(
        call: Call<ResponseBody>,
        liveData: MutableLiveData<T>?,
        onSuccess: ((Response<ResponseBody>) -> Unit)? = null
    ) {
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful && liveData != null) {
                    val responseBody = response.body()?.string()
                    val result: T? = parseResponse(responseBody)
                    liveData.value = result
                }

                // 执行自定义操作
                onSuccess?.invoke(response)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    // 通用方法用于解析响应（根据需要进行调整）
    @Suppress("UNCHECKED_CAST")
    private fun <T> parseResponse(responseBody: String?): T? {
        return responseBody as? T
    }
}