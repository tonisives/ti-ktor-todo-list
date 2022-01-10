import com.tonisives.entities.Todo

interface TodoRepository {
    fun getAllTodos(): List<Todo>
    fun getToDo(id: Int): Todo?
}
