package com.example.test.models

import java.io.Serializable

data class VehicleAdd(
    val userId: String?,
    val vehicleType: String?,
    val plateNumber: String?,
    val make: String?,
    val model: String?,
    val year: String?,
    val vin: String?,
    val fuelType: String?,
    val odometerReading: Double,
    val currentLocation: String?,
    val availability: String?,
    val driver: String?,
    val route: String?,
    val fuelConsumptionRate: Double,
    val imageUrl: String?,
    val seatCapacity:Int
): Serializable {
    val operationArea: String? = null
    val geofenceRadius: Double? = null
    val _id: String? = null
    constructor(
        userId: String?,
        vehicleType: String?,
        plateNumber: String?,
        make: String?,
        model: String?,
        year: String?,
        vin: String?,
        fuelType: String?,
        odometerReading: Double,
        currentLocation: String?,
        availability: String?,
        driver: String?,
        route: String?,
        fuelConsumptionRate: Double,
        imageUrl: String?,
        seatCapacity:Int,
        operationArea:String?,
        geofenceRadius:Double,
    ): this(
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



