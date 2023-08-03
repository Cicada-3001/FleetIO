package com.example.test.models

import java.io.Serializable

data class TripAdd(
    val userId:String,
   val vehicle: String,
   val startTime: String,
   val endTime: String,
): Serializable{
    val _id:String? = null
}

