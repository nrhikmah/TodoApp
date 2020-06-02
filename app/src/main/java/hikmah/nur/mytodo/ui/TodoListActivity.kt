package hikmah.nur.mytodo.ui

import android.app.Activity
import android.app.Dialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import hikmah.nur.mytodo.R
import hikmah.nur.mytodo.data.database.TodoRecord
import hikmah.nur.mytodo.notification.NotificationUtils
import hikmah.nur.mytodo.utils.Constants
import hikmah.nur.mytodo.utils.convertMillis
import hikmah.nur.mytodo.utils.convertNumberToMonthName
import kotlinx.android.synthetic.main.activity_todo_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.todo_item_display_details_dialog.*


class TodoListActivity : AppCompatActivity(), TodoListAdapter.TodoEvents {

    private lateinit var todoViewModel: TodoViewModel
    private lateinit var searchView: SearchView
    private lateinit var todoAdapter: TodoListAdapter

    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_main)

        //Setting up RecyclerView
        rv_todo_list.layoutManager = LinearLayoutManager(this)
        todoAdapter = TodoListAdapter(this)
        rv_todo_list.adapter = todoAdapter

        //Setting up ViewModel and LiveData
        todoViewModel =ViewModelProvider(this).get(TodoViewModel::class.java)
        todoViewModel.getAllTodoList().observe(this, Observer { it ->
            val itemsWithNoDeadline = mutableListOf<TodoRecord>()
            val completedItems = mutableListOf<TodoRecord>()

            for (item in it) {
                if (item.dueTime!!.toInt() == 0 && !item.completed) {
                    itemsWithNoDeadline.add(item)
                } else if (item.completed) {
                    completedItems.add(item)
                }
            }

            for (item in itemsWithNoDeadline) {
                itemsWithNoDeadline.remove(item)
            }

            for (item in completedItems) {
                completedItems.remove(item)
            }

            itemsWithNoDeadline.sortBy { it.dueTime }

            itemsWithNoDeadline.addAll(itemsWithNoDeadline)
            itemsWithNoDeadline.addAll(completedItems)

            todoAdapter.setAllTodoItems(it)

            if (it.size == 0) {
                displayEmptyTaskListImage()
            }
        })

        //FAB click listener
        fab_new_todo.setOnClickListener {
            resetSearchView()
            val intent = Intent(this@TodoListActivity, CreateTodoActivity::class.java)
            startActivityForResult(intent, Constants.INTENT_CREATE_TODO)
        }
    }

    override fun onDeleteClicked(todoRecord: TodoRecord) {
        todoViewModel.deleteTodo(todoRecord)
        NotificationUtils().cancelNotification(todoRecord, this)
    }

    override fun onViewClicked(todoRecord: TodoRecord) {
        resetSearchView()
//        val intent = Intent(this@TodoListActivity, CreateTodoActivity::class.java)
//        intent.putExtra(Constants.INTENT_OBJECT, todoRecord)
//        startActivityForResult(intent, Constants.INTENT_UPDATE_TODO)
        displayEventDetails(todoRecord)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val todoRecord = data?.getParcelableExtra<TodoRecord>(Constants.INTENT_OBJECT)!!
            when (requestCode) {
                Constants.INTENT_CREATE_TODO -> {
                    todoViewModel.saveTodo((todoRecord))
                    hideEmptyTaskListImage()
                }
                Constants.INTENT_UPDATE_TODO -> {
                    todoViewModel.updateTodo(todoRecord)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu?.findItem(R.id.search_todo)
            ?.actionView as SearchView
        searchView.setSearchableInfo(
            searchManager
                .getSearchableInfo(componentName)
        )
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                todoAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                todoAdapter.filter.filter(newText)
                return false
            }

        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.search_todo -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        resetSearchView()
        super.onBackPressed()
    }

    private fun resetSearchView() {
        if (!searchView.isIconified) {
            searchView.isIconified = true
            return
        }
    }


    override fun onCheckClicked(todoItem: TodoRecord) {
        // when complete button is toggled, check current status without updating it and
        // either cancel the alarm or set it.
        // this if also handles the below case
        // if the due is already passed for the unchecked item it does not set a new alarm
        if (!todoItem.completed) {
            NotificationUtils().cancelNotification(todoItem, this)
        } else if (todoItem.completed && todoItem.dueTime!! > 0 && System.currentTimeMillis() < todoItem.dueTime) {
            NotificationUtils().setNotification(todoItem, this)
        }

        todoViewModel.toggleCompleteState(todoItem)
    }

    private fun displayEventDetails(todoItem: TodoRecord) {
        dialog = Dialog(this)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setCancelable(true)
        dialog!!.setContentView(R.layout.todo_item_display_details_dialog)

        dialog!!.tv_todo_title_content.text = todoItem.title
        dialog!!.tv_todo_note_content.text = todoItem.note
        dialog!!.tv_todo_tags_content.text = todoItem.tags

        if (todoItem.dueTime!!.toInt() == 0) {
            dialog!!.tv_todo_due_content.text = getString(R.string.no_due_is_set)
        } else {
            val dateValues = convertMillis(todoItem.dueTime)
            val displayFormat: String
            if (dateValues[4] < 10) {
                displayFormat = String
                    .format(
                        getString(R.string.due_date_minute_less_than_ten),
                        convertNumberToMonthName(dateValues[1]),
                        dateValues[0],
                        dateValues[2],
                        dateValues[3],
                        dateValues[4]
                    )
            } else {
                displayFormat = String
                    .format(
                        getString(R.string.due_date_minute_greater_than_ten),
                        convertNumberToMonthName(dateValues[1]),
                        dateValues[0],
                        dateValues[2],
                        dateValues[3],
                        dateValues[4]
                    )
            }

            dialog!!.tv_todo_due_content.text = displayFormat
        }

        if (todoItem.completed) {
            dialog!!.button_complete_todo_item.text = getString(R.string.mark_as_incomplete)
        } else {
            dialog!!.button_complete_todo_item.text = getString(R.string.mark_as_complete)
        }
        dialog!!.button_complete_todo_item.setOnClickListener {
            if (!todoItem.completed) {
                dialog!!.button_complete_todo_item.text = getString(R.string.mark_as_incomplete)
            } else {
                dialog!!.button_complete_todo_item.text = getString(R.string.mark_as_complete)
            }
            onCheckClicked(todoItem)
        }
        // When user clicks edit, cancel the alarm set for the task and re-create it when coming back from
        // the activity.
        dialog!!.button_edit_todo_item.setOnClickListener {
            NotificationUtils().cancelNotification(todoItem, this)
            val intent = Intent(this@TodoListActivity, CreateTodoActivity::class.java)
            intent.putExtra(Constants.INTENT_OBJECT, todoItem)
            startActivityForResult(intent, Constants.INTENT_UPDATE_TODO)
            dialog!!.dismiss()
        }

        dialog!!.show()
    }

    private fun hideEmptyTaskListImage() {
        if (iv_empty_task_list.visibility == View.VISIBLE) {
            iv_empty_task_list.visibility = View.GONE
        }
    }

    private fun displayEmptyTaskListImage() {
        if (iv_empty_task_list.visibility == View.GONE) {
            iv_empty_task_list.visibility = View.VISIBLE
        }
    }

}



