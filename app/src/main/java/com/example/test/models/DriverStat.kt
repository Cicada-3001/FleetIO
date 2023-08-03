package com.example.test.models

import java.io.Serializable

data class DriverStat(
    val driver: Driver,
    val trips: Int,
    val totalRevenue: Double,
    val avgRevenuePerSeat: Double,
    val avgWeightedAvgRevenuePerSeat: Double
)