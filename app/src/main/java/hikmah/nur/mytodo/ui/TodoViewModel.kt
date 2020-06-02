package hikmah.nur.mytodo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import hikmah.nur.mytodo.data.TodoRepository
import hikmah.nur.mytodo.data.database.TodoRecord

class TodoViewModel(application: Application): AndroidViewModel(application) {
    private val repository: TodoRepository = TodoRepository(application)
    private val allTodoList: LiveData<List<TodoRecord>> = repository.getAllTodoList()

    fun saveTodo(todo: TodoRecord){
        repository.saveTodo(todo)
    }

    fun saveTodoItems(todo: List<TodoRecord>) {
        repository.saveTodoItems(todo)
    }

    fun updateTodo(todo: TodoRecord){
        repository.updateTodo(todo)
    }

    fun deleteTodo(todo: TodoRecord){
        repository.deleteTodo(todo)
    }

    fun toggleCompleteState(todo: TodoRecord) {
        todo.completed = !todo.completed
        repository.updateTodo(todo)
    }

    fun getAllTodoList():LiveData<List<TodoRecord>>{
        return allTodoList
    }

}