package hikmah.nur.mytodo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import hikmah.nur.mytodo.data.TodoRepository
import hikmah.nur.mytodo.data.database.TodoRecord
import java.util.*

class TodoViewModel(application: Application): AndroidViewModel(application) {
    private val repository: TodoRepository = TodoRepository(application)
    private val allTodoList: LiveData<List<TodoRecord>> = repository.getAllTodoList()

    fun saveTodo(todo: TodoRecord){
        repository.saveTodo(todo)
    }

    fun updateTodo(todo: TodoRecord){
        repository.updateTodo(todo)
    }

    fun deleteTodo(todo: TodoRecord){
        repository.deleteTodo(todo)
    }

    fun dueTodo(due: Date){
        repository.dueTodo(due)
    }
    fun getAllTodoList():LiveData<List<TodoRecord>>{
        return allTodoList
    }

}