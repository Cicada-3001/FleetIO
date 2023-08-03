package com.example.test.models

import java.io.Serializable


data class Driver(
    val _id: String?,
    val userId: String?,
    val firstName: String?,
    val lastName: String?,
    val licenseNumber: String?,
    val licenseExpiry: String?,
    val dateOfBirth: String?,
    val phoneNumber: String?,
    val email: String?,
    val imageUrl: String?,
    val vehicle: VehicleAdd?
    )








