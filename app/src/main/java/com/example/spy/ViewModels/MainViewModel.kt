package com.example.spy.ViewModels

import Model.Vehicle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spy.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel (private val repository: Repository): ViewModel(){
    val vehicleResponse: MutableLiveData<Response<ArrayList<Vehicle>>> = MutableLiveData()
    /*    get requests    */
    fun getVehicleByVin(vin: String) {
        viewModelScope.launch {
            val response: Response<ArrayList<Vehicle>> = repository.getVehicleByVin(vin)
            vehicleResponse.value= response
        }
    }
}