package com.tonisives.repository

class InMemoryUserRepository : UserRepository {
    // map of credentials(username and password) and corresponding users
    // in a db should store password in a hashed format
    private val credentialsToUsers = mapOf(
        "admin:admin" to UserRepository.User(1, "admin"),
        "max:1234" to UserRepository.User(2, "max")
    )

    override fun getUser(username: String, password: String): UserRepository.User? {
        return credentialsToUsers["$username:$password"]
        return null
    }
}