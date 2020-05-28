package hikmah.nur.mytodo.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.Room


@Database(entities = [TodoRecord::class], version = 1, exportSchema = false)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao

    companion object{
        private var INSTANCE: TodoDatabase? = null

        fun getInstance(context: Context):TodoDatabase? {
            if (INSTANCE == null){
                synchronized(TodoDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context,TodoDatabase::class.java,"todo_db").build()
                }
            }
            return INSTANCE
        }
    }

}