import com.tonisives.entities.Todo

interface TodoRepository {
    fun getAllTodos(): List<Todo>
    fun getToDo(id: Int): Todo?

    /**
     * @return Todo with id
     */
    fun addTodo(draft: TodoDraft): Todo

    /**
     * @return whether remove operation was successful
     */
    fun removeTodo(id: Int): Boolean


    fun updateTodo(id: Int, draft: TodoDraft): Boolean
}
