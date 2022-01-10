import com.tonisives.entities.Todo

class InMemoryTodoRepository : TodoRepository {
    val todos = mutableListOf(
        Todo(1, "Plan content for video #2", true),
        Todo(2, "Water the flowers", false),
        Todo(3, "Go walk on the beach", false)
    )

    override fun getAllTodos(): List<Todo> {
        return todos
    }

    override fun getToDo(id: Int): Todo? {
        return todos.firstOrNull { it.id == id }
    }
}
