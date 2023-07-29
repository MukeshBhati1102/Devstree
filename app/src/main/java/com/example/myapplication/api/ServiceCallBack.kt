package com.example.myapplication.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface ServiceCallBack {

    @GET("directions/json")
    open fun getStringResponse(@QueryMap params : Map<String, String> ): Call<String?>?

//    @GET("directions/{endpoint}")
//    open fun getStringResponse(@Path("endpoint")  endpoint :String): Call<String?>?

}