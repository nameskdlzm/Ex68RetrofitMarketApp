package com.mrhi2024.ex68retrofitmarketapp

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap

interface RetrofitService {

    //1. POST 방식으로 데이터를 보내기
    @Multipart
    @POST("/05Retrofit/insertDB.php")
    fun postDataToServer(@PartMap dataPart:Map<String , String>,
                         @Part filePart: MultipartBody.Part?) : Call<String>

    //2. GET 방식으로 json array데이터를 받아와서 파싱하여 결과 받는 코드 만들어줘
    @GET("/05Retrofit/loadDB.php")
    fun loadDataFromServer() : Call<List<MarketItem>>

}