package org.bitc.petpalapp

import org.bitc.petpalapp.ui.hospital.HospitalListModel
import retrofit2.Call
import retrofit2.http.GET

interface INetworkService {
    @GET("hospitals")
    fun getHospitalList(): Call<HospitalListModel>
}