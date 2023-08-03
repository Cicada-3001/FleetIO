package com.example.test.models

import java.io.Serializable

data class VehicleStat(
    val vehicle: Vehicle,
    val trips: Int,
    val totalFuelCost: Double,
    val totalFareRevenue: Double,
    val grossRevenue: Double,
    val totalWeightedRevenue: Double,
    val totalDistance: Double,
    val totalTime: String,
    val avgRevenuePerSeat: Double,
    val avgWeightedRevenuePerSeat:Double
)