package com.example.test.callback

import com.example.test.models.VehicleGeoModel

interface FirebaseDriverInfoListener {

    fun onDriverInfoLoadSuccess(driverGeoModel: VehicleGeoModel){
    }




}