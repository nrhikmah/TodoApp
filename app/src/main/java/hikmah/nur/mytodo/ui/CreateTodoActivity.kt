package hikmah.nur.mytodo.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import hikmah.nur.mytodo.R
import hikmah.nur.mytodo.data.database.TodoRecord
import hikmah.nur.mytodo.utils.Constants
import kotlinx.android.synthetic.main.activity_create_todo.*


class CreateTodoActivity : AppCompatActivity(){

    var todoRecord: TodoRecord? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_todo)

        //Prepopulate existing title and content from intent
        val intent = intent
        if (intent != null && intent.hasExtra(Constants.INTENT_OBJECT)) {
            val todoRecord: TodoRecord = intent.getParcelableExtra(Constants.INTENT_OBJECT)
            this.todoRecord = todoRecord
            prePopulateData(todoRecord)
        }
        title = if (todoRecord != null) getString(R.string.viewOrEditTodo) else getString(R.string.createTodo)
    }

    private fun prePopulateData(todoRecord: TodoRecord) {
        et_todo_title_content.setText(todoRecord.title)
        et_todo_note_content.setText(todoRecord.note)
//        et_todo_due_content.setText(todoRecord.due)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflate = menuInflater
        menuInflate.inflate(R.menu.menu_save, menu)
        return  true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.save_todo -> {
                saveTodo()
            }
        }
        return true
    }

    /**
     * Sends the updated information back to calling Activity
     * */

    private fun saveTodo(){
        if (validateFields()){
            val id = if (todoRecord != null) todoRecord?.id else null
            val todo = TodoRecord(
                id = id,
                title = et_todo_title_content.text.toString(),
                note = et_todo_note_content.text.toString()
            )
            val intent = Intent()
            intent.putExtra(Constants.INTENT_OBJECT,todo)
            setResult(RESULT_OK,intent)
            finish()
        }
    }

    /**
     * Validation of EditText
     * */
    private fun validateFields(): Boolean {
        if (et_todo_title_content.text.isEmpty()){
            til_todo_title.error = "Please enter title"
            et_todo_title_content.requestFocus()
            return false
        }
        if (et_todo_note_content.text.isEmpty()){
            til_todo_note.error = "Please enter note for this"
            et_todo_note_content.requestFocus()
            return false
        }
        return true
    }
}

