package com.example.test.models

import java.io.Serializable

data class User(
    val fullname: String,
    val phone: String,
    val email: String,
    val password: String,
): Serializable {
    val created: String? = null
    val lastLogin: String? = null
    val _id: String? = null
    constructor(
        _id: String,
        fullname: String,
        phone: String,
        email: String,
        password: String,
        created: String,
        lastLogin: String,
    ): this(
    fullname,
    phone,
    email,
    password
    ) }
