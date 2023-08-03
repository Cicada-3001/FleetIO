package com.example.test.models

import com.firebase.geofire.GeoLocation

class VehicleGeoModel {
    var key: String? = null
    var geoLocation: GeoLocation? = null
    var vehicleInfoModel: VehicleInfoModel? = null

    constructor(key:String, geoLocation: GeoLocation){
        this.key= key
        this.geoLocation = geoLocation
    }

}