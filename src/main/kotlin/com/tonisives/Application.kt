package com.tonisives

import com.tonisives.authentication.JwtConfig
import com.tonisives.koin.appModules
import com.tonisives.repository.InMemoryTodoRepository
import com.tonisives.repository.InMemoryUserRepository
import com.tonisives.repository.TodoRepository
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.request.*
import io.ktor.serialization.*

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.Koin

import org.slf4j.event.Level

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        main()
    }.start(wait = true)
}

fun Application.main() {
    install(Koin) {
        modules(appModules)
    }
    val jwtSecret = System.getenv("KTOR_TODOLIST_JWT_SECRET")
    configureLogging()
    configureSerialization()
    val config = configureAuth(jwtSecret)
    configureRouting(config)
}

fun Application.configureAuth(secret: String): JwtConfig {
    val jwtConfig = JwtConfig(secret)

    install(Authentication) {
        jwt {
            jwtConfig.configureKtorFeature(this)
        }
    }

    return jwtConfig
}

fun Application.configureLogging() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
        })
    }
}