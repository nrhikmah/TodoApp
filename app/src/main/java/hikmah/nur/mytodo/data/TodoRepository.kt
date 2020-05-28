package hikmah.nur.mytodo.data

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import hikmah.nur.mytodo.data.database.TodoDao
import hikmah.nur.mytodo.data.database.TodoDatabase
import hikmah.nur.mytodo.data.database.TodoRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TodoRepository(application: Application) {
    private val todoDao: TodoDao
    private val allTodos: LiveData<List<TodoRecord>>

    init {
        val database = TodoDatabase.getInstance(application.applicationContext)
        todoDao = database!!.todoDao()
        allTodos = todoDao.getAllTodolist()
    }

    fun saveTodo(todo: TodoRecord)= runBlocking {
        this.launch(Dispatchers.IO) {
            todoDao.saveTodo(todo)
        }
    }

    fun updateTodo(todo: TodoRecord) = runBlocking {
        this.launch(Dispatchers.IO) {
            todoDao.updateTodo(todo)
        }
    }

    fun deleteTodo(todo: TodoRecord)  {
        runBlocking{
            this.launch(Dispatchers.IO) {
                todoDao.deleteTodo(todo)
            }
        }
    }

    fun getAllTodoList(): LiveData<List<TodoRecord>> {
        return allTodos
    }

}