package com.tonisives

import TodoDraft
import com.tonisives.entities.Todo
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.junit.Test

class RoutingTest {

    @Test
    fun testAllTodos() = testApplication {
        application {
            configureSerialization()
            configureRouting()
        }

        client.get("/todos") {

        }.let {
            val todos = Json.decodeFromString<List<Todo>>(String(it.readBytes()))
            assert(it.call.response.status == HttpStatusCode.OK)
            assert(todos.size == 3)
        }
    }

    @Test
    fun testSingleTodo() = testApplication {
        application {
            configureSerialization()
            configureRouting()
        }

        client.get("/todos/2") {
        }.let {
            assert(it.call.response.status == HttpStatusCode.OK)
            val todo = Json.decodeFromString<Todo>(String(it.readBytes()))
            assert(todo.id == 2)
        }
    }

    @Test
    fun testUpdate() = testApplication {
        application {
            install(ContentNegotiation) {
                json(Json)
            }
            configureRouting()
        }

        val todoDraft = TodoDraft("updated", true)
        val bytesBody = Json.encodeToString(todoDraft)

        client.post("/todos/2") {
            contentType(ContentType.Application.Json)
            setBody(bytesBody.encodeToByteArray())
        }.let {
            assert(it.call.response.status == HttpStatusCode.OK)

            val updatedResponse = client.get("todos/2")
            val todo = Json.decodeFromString<Todo>(String(updatedResponse.readBytes()))
            assert(todo.title == "updated")
            assert(todo.done)
        }
    }
}