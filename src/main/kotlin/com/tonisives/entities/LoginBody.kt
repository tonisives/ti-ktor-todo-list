package com.tonisives.entities

import kotlinx.serialization.Serializable

@Serializable
data class LoginBody(
    val username: String,
    val password: String
)