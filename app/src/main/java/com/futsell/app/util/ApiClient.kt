package com.futsell.app.util

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



class ApiClient {

    public var retrofit: Retrofit? = null

    fun getClient() : Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.42.162:45455/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        return retrofit!!
    }
}