package com.tonisives

import com.tonisives.repository.InMemoryTodoRepository
import com.tonisives.repository.MySqlTodoRepository
import com.tonisives.repository.TodoRepository
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import kotlinx.serialization.json.Json

import org.slf4j.event.Level

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureLogging()
        configureSerialization()
        configureRouting()

    }.start(wait = true)
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

val repository: TodoRepository by lazy {
    MySqlTodoRepository()
}
