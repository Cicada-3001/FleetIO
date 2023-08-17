package com.example.test.models

import java.io.Serializable

data class DriverStat(
    val driver: DriverAdd,
    val trips: Int,
    val totalRevenue: Double,
    val avgRevenuePerSeat: Double,
    val avgWeightedAvgRevenuePerSeat: Double
)