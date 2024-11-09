package com.chiuxah.wanwandongting.logic.network.serviceCreator

import com.chiuxah.wanwandongting.MyApplication
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object SearchServiceCreator {
    val retrofit = Retrofit.Builder()
        .baseUrl(MyApplication.qmApi)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()


    fun <T> create(service: Class<T>): T = retrofit.create(service)
    inline fun <reified  T> create() : T = create(T::class.java)
}