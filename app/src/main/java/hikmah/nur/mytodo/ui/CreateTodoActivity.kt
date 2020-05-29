package hikmah.nur.mytodo.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import hikmah.nur.mytodo.R
import hikmah.nur.mytodo.data.database.TodoRecord
import hikmah.nur.mytodo.notification.NotificationUtils
import hikmah.nur.mytodo.utils.Constants
import hikmah.nur.mytodo.utils.convertMillis
import hikmah.nur.mytodo.utils.convertNumberToMonthName
import hikmah.nur.mytodo.utils.dateToMillis
import kotlinx.android.synthetic.main.activity_create_todo.*
import java.time.ZonedDateTime
import java.util.*


class CreateTodoActivity : AppCompatActivity(){

    private var mDueMonth: Int = 0
    private var mDueDay: Int = 0
    private var mDueYear: Int = 0
    private var mDueHour: Int = 0
    private var mDueMinute: Int = 0

    private var dueDate: Long = 0

    private var dateSelected = false
    private var timeSelected = false

    var todoRecord: TodoRecord? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_todo)

        //Prepopulate existing title and content from intent
        val intent = intent
        if (intent != null && intent.hasExtra(Constants.INTENT_OBJECT)) {
            val todoRecord: TodoRecord = intent.getParcelableExtra(Constants.INTENT_OBJECT)
            this.todoRecord = todoRecord
//            prePopulateData(todoRecord)

            if (todoRecord.dueTime!!.toInt() != 0) {
                dateSelected = true
                timeSelected = true
                val list = convertMillis(todoRecord.dueTime)

                mDueDay = list[0]
                mDueMonth = list[1]
                mDueYear = list[2]
                mDueHour = list[3]
                mDueMinute = list[4]
            }

            fillUIWithItemData(todoRecord)
        }

        tv_todo_due_date.setOnClickListener {
            showDatePickerDialog()
        }

        tv_todo_due_time.setOnClickListener{
            showTimePickerDialog()
        }

        title = if (todoRecord != null) getString(R.string.viewOrEditTodo) else getString(R.string.createTodo)
    }

//    private fun prePopulateData(todoRecord: TodoRecord) {
//        et_todo_title_content.setText(todoRecord.title)
//        et_todo_note_content.setText(todoRecord.note)
//        et_todo_due_content.setText(todoRecord.due)
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflate = menuInflater
        menuInflate.inflate(R.menu.menu_save, menu)
        return  true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.save_todo -> {
                setDueDateInMillis()
                saveTodo()
            }
        }
        return true
    }

    /**
     * Sends the updated information back to calling Activity
     * */

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveTodo(){


        if (validateFields()){
            val current = ZonedDateTime.now()
            val millis = current.toInstant().epochSecond
            val createdDate=millis.toInt()
            val id = if (todoRecord != null) todoRecord?.id else null
            val todo = TodoRecord(
                id = id,
                title = et_todo_title_content.text.toString(),
                note = et_todo_note_content.text.toString(),
                tags = et_todo_tags.text.toString(),
                dueTime = dueDate,
                created = createdDate,
                completed = todoRecord?.completed ?: false
            )
            val intent = Intent()
            intent.putExtra(Constants.INTENT_OBJECT,todo)
            setResult(RESULT_OK,intent)

            if (todo.dueTime!! > 0){
                NotificationUtils().setNotification(todo, this)
            }


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
        if (et_todo_tags.text.isEmpty()) {
            til_todo_tags.error = "Please provide at least one tag"
            et_todo_tags.requestFocus()
            return false
        }
        Toast.makeText(this, "Saved successfully.", Toast.LENGTH_SHORT).show()
        return true
    }

    private fun setDueDateInMillis() {
        if (timeSelected && !dateSelected) {
            mDueYear = Calendar.getInstance().get(Calendar.YEAR)
            mDueMonth = Calendar.getInstance().get(Calendar.MONTH)
            mDueDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

            dueDate = dateToMillis(mDueDay, mDueMonth, mDueYear, mDueMinute, mDueHour)
        } else if (!timeSelected && dateSelected) {
            mDueHour = 0
            mDueMinute = 0

            dueDate = dateToMillis(mDueDay, mDueMonth, mDueYear, mDueMinute, mDueHour)
        }else if (timeSelected && dateSelected) {
            dueDate = dateToMillis(mDueDay, mDueMonth, mDueYear, mDueMinute, mDueHour)
        }
    }

    private fun showDatePickerDialog(){
        mDueDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        mDueMonth = Calendar.getInstance().get(Calendar.MONTH)
        mDueYear = Calendar.getInstance().get(Calendar.YEAR)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener{_, year, monthOfYear, dayOfMonth ->
                tv_todo_due_date.text =  ("""Due Date: ${convertNumberToMonthName(monthOfYear)} $dayOfMonth $year""")
                mDueDay = dayOfMonth
                mDueMonth = monthOfYear
                mDueYear = year
                dateSelected = true
            },
            mDueYear,
            mDueMonth,
            mDueDay
        )
        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        mDueHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        mDueMinute = Calendar.getInstance().get(Calendar.MINUTE)

        val timePickerDialog =
            TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                mDueHour = hourOfDay
                mDueMinute = minute

                val displayFormat: String = if (mDueMinute < 10 && mDueHour < 10) {
                    """Due time: 0$hourOfDay : 0$mDueMinute"""
                } else {
                    if (mDueMinute > 10 ) {
                        """Due time: $hourOfDay : $mDueMinute"""
                    } else {
                        """Due time: $hourOfDay : 0$mDueMinute"""
                    }
                }

                tv_todo_due_time.text = displayFormat
                timeSelected = true

            }, mDueHour, mDueMinute, true)
        timePickerDialog.show()
    }

    private fun fillUIWithItemData(todoRecord: TodoRecord){
        et_todo_title_content.setText(todoRecord.title,TextView.BufferType.EDITABLE)
        et_todo_note_content.setText(todoRecord.note, TextView.BufferType.EDITABLE)
        et_todo_tags.setText(todoRecord.tags, TextView.BufferType.EDITABLE)

        if (todoRecord.dueTime!!.toInt() !=0){
            val dateValues = convertMillis(todoRecord.dueTime)

            val dueMonth = convertNumberToMonthName(dateValues[1])

            val dueYear = dateValues[2].toString()

            val dueHour = if (dateValues[3] < 10) {
                "0${dateValues[3]}"
            } else {
                "${dateValues[3]}"
            }

            val dueMinute = if (dateValues[4] < 10) {
                "0${dateValues[4]}"
            } else {
                "${dateValues[4]}"
            }

            tv_todo_due_date.text = """${dueMonth} ${dateValues[0]} ${dueYear}"""
            tv_todo_due_time.text = """${dueHour} : ${dueMinute}"""
        }
    }
}

