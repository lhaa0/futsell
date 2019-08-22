package com.futsell.app.util

import com.futsell.app.model.ModelFutsal
import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {

    @GET("tb_futsal")
    fun tb_futsal() : Call<ArrayList<ModelFutsal>>
}