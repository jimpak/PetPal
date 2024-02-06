package org.bitc.petpalapp

import retrofit2.Call
import retrofit2.http.GET

interface INetworkService {
    @GET("hospitals")
    fun getHospitalList(): Call<String>
}