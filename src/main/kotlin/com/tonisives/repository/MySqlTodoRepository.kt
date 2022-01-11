package com.tonisives.repository

import TodoDraft
import com.tonisives.database.DatabaseManager
import com.tonisives.entities.Todo

class MySqlTodoRepository : TodoRepository {
    private val database = DatabaseManager()

    override fun getAllTodos(): List<Todo> {
        return database.getAllTodos().map { Todo(it.id, it.title, it.done) }
    }

    override fun getTodo(id: Int): Todo? {
        return database.getTodo(id)
            ?.let { Todo(it.id, it.title, it.done) }
    }

    override fun addTodo(draft: TodoDraft): Todo {
        return database.addTodo(draft)
    }

    override fun removeTodo(id: Int): Boolean {
        return database.removeTodo(id)
    }

    override fun updateTodo(id: Int, draft: TodoDraft): Boolean {
        return database.updateTodo(id, draft)
    }

}
