package org.bitc.petpalapp

import org.bitc.petpalapp.ui.hospital.HospitalListModel
import org.bitc.petpalapp.ui.hospital.HospitalModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface INetworkService {
    @GET("hospitals")
    fun getHospitalList(): Call<HospitalListModel>
    @GET("hospital/{hno}")
    fun getHospital(@Path("hno") hno: Long): Call<HospitalModel>
}