package com.example.test.models

import java.io.Serializable

data class Counts (
    val _id: String?,
    val needMaintenance: Int,
    val expiredLicenses: Int,
    val onRoute: Int,
    val vehicles: Int,
    val drivers: Int,
    val routes: Int,
    val trips: String?,

): Serializable


