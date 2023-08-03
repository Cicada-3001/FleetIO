package com.example.test.models

import java.io.Serializable

data class Maintenance(
    val _id:String,
    val userId:String,
    val vehicle: Vehicle,
    val maintenanceType: String,
    val date: String,
    val cost: Double,
    val description: String,
    val maintained: Boolean
): Serializable



