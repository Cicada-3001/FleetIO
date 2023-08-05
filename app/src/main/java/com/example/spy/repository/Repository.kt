package com.example.spy.repository

import Model.Vehicle
import Services.ApiService.RetrofitInstance
import androidx.lifecycle.MutableLiveData
import retrofit2.Response

class Repository {
    val vehicleResponse: MutableLiveData<ArrayList<Vehicle>> = MutableLiveData()
    suspend fun getVehicleByVin(vin:String): Response<ArrayList<Vehicle>> {
        return RetrofitInstance.api.getVehiclesByVin(vin)
    }

}