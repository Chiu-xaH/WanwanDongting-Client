package com.chiuxah.wanwandongting.logic.network.serviceCreator

import androidx.compose.ui.res.stringResource
import com.chiuxah.wanwandongting.MyApplication
import com.chiuxah.wanwandongting.R
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object QMXServiceCreator {
    val retrofit = Retrofit.Builder()
        .baseUrl(MyApplication.qmxApi)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()


    fun <T> create(service: Class<T>): T = retrofit.create(service)
    inline fun <reified  T> create() : T = create(T::class.java)
}