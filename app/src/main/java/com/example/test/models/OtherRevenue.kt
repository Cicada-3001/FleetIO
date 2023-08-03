package com.example.test.models

import java.io.Serializable

data class OtherRevenue(
    var userId:String,
    var description: String,
    var amount: Double,
    var created:String
): Serializable {
    val _id: String?= null
    constructor(
        _id:String,
        userId:String,
        description: String,
        amount: Double,
        created: String,
    ) : this(
        userId,
        description,
        amount,
        created)
}

