package com.example.test.models

import java.io.Serializable

data class MaintenanceAdd(
    val userId:String,
    val vehicle: String,
    val maintenanceType: String,
    val date: String,
    val cost: Double,
    val description: String,
    val maintained: Boolean
)

