package com.example.test.util

import com.example.test.models.User

class Constants {
    companion object {
        // const val BASE_URL = "http://192.168.0.14:8080/api/v1/"
        // const val BASE_URL = "http://192.168.111.189:8080/api/v1/"
        //const val BASE_URL = "http://192.168.0.14:8080/"
        const val BASE_URL = "http://192.168.0.14:8080/"
        lateinit var currentUser: User
        val USER_INFO_REFERENCE: String = "DriverInfo"
        var userId: String? = "64181c109e84129907e3ac13"
    }
}