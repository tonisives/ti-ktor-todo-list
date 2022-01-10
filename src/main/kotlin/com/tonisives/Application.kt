package com.tonisives

import InMemoryTodoRepository
import TodoRepository
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

import org.slf4j.event.Level

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureLogging()
        configureSerialization()

        // Starting point for a Ktor app:
        routing {
            get("/") {
                call.respondText("Hello World!")
            }

            get("/todos") {
                call.respond(repository.getAllTodos())
            }

            get("/todos/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                id?.let {
                    repository.getToDo(it)?.let { todo ->
                        call.respond(todo)
                    }
                }
            }

            post("/todos") {

            }

            post("/todos/{id}") {
                val id = call.parameters["id"]
                // edit a single todo
            }

            delete("/todos/{id}") {

            }
        }
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

    routing {
        get("/json/kotlinx-serialization") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}

val repository: TodoRepository by lazy {
    InMemoryTodoRepository()
}
