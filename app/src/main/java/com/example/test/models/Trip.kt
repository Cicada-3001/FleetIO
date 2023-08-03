package com.example.test.models

import java.io.Serializable

data class Trip(
    val userId:String,
   val vehicle: Vehicle,
   val startTime: String,
   val endTime: String,
): Serializable {
   val _id: String?= null
   constructor(
      _id:String,
      userId:String,
      vehicle: Vehicle,
      startTime: String,
      endTime: String,
   ) : this(
       userId,
       vehicle,
       startTime,
       endTime,) }

