package com.tonisives

import com.tonisives.authentication.JwtConfig
import com.tonisives.koin.appModules
import com.tonisives.repository.InMemoryTodoRepository
import com.tonisives.repository.InMemoryUserRepository
import com.tonisives.repository.TodoRepository
import com.tonisives.repository.UserRepository
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module
import org.koin.ktor.ext.Koin

class RoutesTest {

    @Before
    fun before() {

    }

    @Test
    fun testRoot() {
        val jwtSecret = "jwt secret"
        fun Application.test() {
            configureSerialization()
            val config = configureAuth(jwtSecret)
            configureRouting(config)
        }

        withTestApplication(Application::test) {
            handleRequest(HttpMethod.Get, "/").apply {
                assert(response.status() == HttpStatusCode.OK)
                assert(response.content == "Hello World!")
            }
        }
    }

    @Test
    fun testAllTodos() {
        var authToken = ""
        fun Application.test() {
            install(Koin) {
                modules(
                    module {
                        single { InMemoryUserRepository() as UserRepository }
                        single { InMemoryTodoRepository() as TodoRepository }
                    }
                )
            }
            configureSerialization()
            val auth = configureAuth("jwt")
            authToken = auth.generateToken(JwtConfig.JwtUser(1, "pass"))
            configureRouting(auth)

        }

        withTestApplication(Application::test) {
            handleRequest(HttpMethod.Get, "/todos") {
                addHeader("Authorization", "Bearer $authToken")
            }.apply {
                assert(response.status() == HttpStatusCode.OK)
                val json = Json.decodeFromString<JsonArray>(response.content!!)
                assert(json.size == 3)
            }
        }
    }

/*
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

    @Test
    fun verifyNotFoundErrorReturned() = testApplication {
        application {
            configureSerialization()
            configureRouting()
        }

        try {
            val response = client.get("/todos/8") {

            }
        } catch (e: ClientRequestException) {
            assert(e.response.status == HttpStatusCode.NotFound)
        }
    }*/
}