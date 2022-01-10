package com.tonisives

import com.tonisives.entities.Todo
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
            val todos = listOf(
                Todo(1, "Plan content for video #2", true),
                Todo(2, "Water the flowers", false),
                Todo(3, "Go walk on the beach", false)
            )

            get("/") {
                call.respondText("Hello World!")
            }

            get("/todos") {
                call.respond(todos)
            }

            get("/todos/{id}") {
                val id = call.parameters["id"]
                call.respondText("Todolist Details for Todo item $id")
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