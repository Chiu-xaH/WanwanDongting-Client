package com.cxh.qqmusictp.logic.network.serviceCreator

import com.cxh.qqmusictp.MyApplication
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ResultServiceCreator {
    //val Client = OkHttpClient.Builder()
    //.build()

    val retrofit = Retrofit.Builder()
        .baseUrl(MyApplication.ResultURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    fun <T> create(service: Class<T>): T = retrofit.create(service)
    inline fun <reified  T> create() : T = create(T::class.java)
}