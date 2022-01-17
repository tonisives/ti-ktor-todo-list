package com.tonisives

import TodoDraft
import com.tonisives.authentication.JwtConfig
import com.tonisives.entities.LoginBody
import com.tonisives.repository.TodoRepository
import com.tonisives.repository.UserRepository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

import io.ktor.util.pipeline.*
import org.koin.ktor.ext.inject

fun Application.configureRouting(jwtConfig: JwtConfig) {
    val userRepository by inject<UserRepository>()
    val repository by inject<TodoRepository>()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        post("/login") {
            val loginBody = call.receive<LoginBody>()
            // store all users and see if one user matches the input
            val user = userRepository.getUser(loginBody.username, loginBody.password)

            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials!")
                return@post
            }

            val token = jwtConfig.generateToken(
                JwtConfig.JwtUser(user.userId, user.username)
            )

            call.respond(token)
        }

        authenticate {
            get("/me") {
                // return information about the user
                val user = call.authentication.principal as JwtConfig.JwtUser
                call.respond(user)
            }

            get("/todos") {
                call.respond(repository.getAllTodos())
            }

            get("/todos/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    respondInvalidParameter()
                    return@get
                }

                repository.getTodo(id)?.let { todo ->
                    call.respond(todo)
                } ?: run {
                    respondTodoNotFound(id)
                }
            }

            post("/todos") {
                val todoDraft = call.receive<TodoDraft>()
                val todo = repository.addTodo(todoDraft)
                call.respond(todo)
            }

            // edit a single todo
            post("/todos/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()

                if (id == null) {
                    respondInvalidParameter()
                    return@post
                }

                val todoDraft = call.receive<TodoDraft>()
                val updated = repository.updateTodo(id, todoDraft)

                if (updated) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    respondTodoNotFound(id)
                }
            }

            delete("/todos/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()

                if (id == null) {
                    respondInvalidParameter()
                    return@delete
                }

                val deleted = repository.removeTodo(id)

                if (deleted) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    respondTodoNotFound(id)
                }
            }
        }

    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.respondInvalidParameter() {
    call.respond(HttpStatusCode.BadRequest, "Invalid id paramater")
}

private suspend fun PipelineContext<Unit, ApplicationCall>.respondTodoNotFound(id: Int?) {
    call.respond(HttpStatusCode.NotFound, "Found no todo with the id $id")
}
