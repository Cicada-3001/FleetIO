package com.example.test.models

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class Vehicle(
    val _id: String?,
    val userId: String?,
    val vehicleType: String?,
    val plateNumber: String?,
    val make: String?,
    val model: String?,
    val year: String?,
    val vin: String?,
    val fuelType: FuelType?,
    val odometerReading: Double,
    val currentLocation: String?,
    val availability: String?,
    val driver: DriverAdd?,
    val route: Route?,
    val fuelConsumptionRate: Double,
    val imageUrl: String?,
    val seatCapacity:Int,
):Serializable{
    val operationArea: String? = null
    val geofenceRadius: Double? = null
    constructor(
       _id: String?,
       userId: String?,
       vehicleType: String?,
       plateNumber: String?,
       make: String?,
       model: String?,
       year: String?,
       vin: String?,
       fuelType: FuelType?,
       odometerReading: Double,
       currentLocation: String?,
       availability: String?,
       driver: DriverAdd?,
       route: Route?,
       fuelConsumptionRate: Double,
       imageUrl: String?,
       seatCapacity:Int,
       operationArea:String?,
       geofenceRadius:Double
    ): this(
        _id,
        userId,
        vehicleType,
        plateNumber,
        make,
        model,
        year,
        vin,
        fuelType,
        odometerReading,
        currentLocation,
        availability,
        driver,
        route,
        fuelConsumptionRate,
        imageUrl,
        seatCapacity,
    )
}


