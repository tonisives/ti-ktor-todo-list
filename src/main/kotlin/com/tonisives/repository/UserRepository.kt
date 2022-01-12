package com.tonisives.repository

interface UserRepository {
    fun getUser(username: String, password: String) : User?

    data class User(
        val userId: Int,
        val username: String
        // password not stored in an object that is returned to the user
    )
}