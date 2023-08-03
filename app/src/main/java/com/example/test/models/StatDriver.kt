package com.example.test.models

data class StatDriver(
    val driver: Driver,
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
