package com.tonisives

import TodoDraft
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*

fun Application.configureRouting() {
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

private suspend fun PipelineContext<Unit, ApplicationCall>.respondInvalidParameter() {
    call.respond(HttpStatusCode.BadRequest, "Invalid id paramater")
}

private suspend fun PipelineContext<Unit, ApplicationCall>.respondTodoNotFound(id: Int?) {
    call.respond(HttpStatusCode.NotFound, "Found no todo with the id $id")
}
