package hikmah.nur.mytodo.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
interface TodoDao {
    @Insert
    suspend fun saveTodo(todoRecord: TodoRecord)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun dueTodo(due: Date)

    @Delete
    suspend fun deleteTodo(todoRecord: TodoRecord)

    @Update
    suspend fun updateTodo(todoRecord: TodoRecord)

    @Query("SELECT * FROM todo ORDER BY id DESC")
    fun getAllTodolist():LiveData<List<TodoRecord>>


}