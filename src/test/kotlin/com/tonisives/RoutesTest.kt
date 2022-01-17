package com.tonisives

import com.tonisives.authentication.JwtConfig
import com.tonisives.entities.Todo
import com.tonisives.repository.InMemoryTodoRepository
import com.tonisives.repository.InMemoryUserRepository
import com.tonisives.repository.TodoRepository
import com.tonisives.repository.UserRepository
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
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
                modules(module {
                    single { InMemoryUserRepository() as UserRepository }
                    single { InMemoryTodoRepository() as TodoRepository }
                })
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

    @Test
    fun testGetSingleTodo() {
        var authToken = ""

        val mockRepo = mockk<TodoRepository> {
            every { getTodo(any()) } returns Todo(1, "test title", false)
        }

        fun Application.test() {
            install(Koin) {
                modules(module {
                    single { InMemoryUserRepository() as UserRepository }
                    single { mockRepo }
                })
            }
            configureSerialization()
            val auth = configureAuth("jwt")
            authToken = auth.generateToken(JwtConfig.JwtUser(1, "pass"))
            configureRouting(auth)

        }

        withTestApplication(Application::test) {
            handleRequest(HttpMethod.Get, "/todos/1") {
                addHeader("Authorization", "Bearer $authToken")
            }.apply {
                assert(response.status() == HttpStatusCode.OK)
                val json = Json.decodeFromString<Todo>(response.content!!)
                assert(json.title == "test title")
            }
        }
    }

    @Test
    fun verifyNotFoundErrorReturned() {
        var authToken = ""

        val mockRepo = mockk<TodoRepository> {
            every { getTodo(any()) } returns null
        }

        fun Application.test() {
            install(Koin) {
                modules(module {
                    single { InMemoryUserRepository() as UserRepository }
                    single { mockRepo }
                })
            }
            configureSerialization()
            val auth = configureAuth("jwt")
            authToken = auth.generateToken(JwtConfig.JwtUser(1, "pass"))
            configureRouting(auth)

        }

        withTestApplication(Application::test) {
            handleRequest(HttpMethod.Get, "/todos/8") {
                addHeader("Authorization", "Bearer $authToken")
            }.apply {
                assert(response.status() == HttpStatusCode.NotFound)
            }
        }
    }
}