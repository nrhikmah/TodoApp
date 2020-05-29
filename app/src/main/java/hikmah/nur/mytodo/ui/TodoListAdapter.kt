package hikmah.nur.mytodo.ui

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import hikmah.nur.mytodo.R
import hikmah.nur.mytodo.data.database.TodoRecord
import hikmah.nur.mytodo.utils.convertMillis
import hikmah.nur.mytodo.utils.convertNumberToMonthName
import kotlinx.android.synthetic.main.todo_item.view.*
import java.util.*

class TodoListAdapter(todoEvents: TodoEvents): RecyclerView.Adapter<TodoListAdapter.ViewHolder>(), Filterable {
    private var todoList : List<TodoRecord> = arrayListOf()
    private var filteredTodoList : List<TodoRecord> = arrayListOf()
    private val listener: TodoEvents = todoEvents

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
         val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = filteredTodoList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredTodoList[position], listener)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(todo: TodoRecord, listener: TodoEvents){
            itemView.tv_item_title.text=todo.title
            itemView.checkbox_item.isChecked = todo.completed

            if (todo.completed){
                // Strike through the text to give an indicator that task is completed.
                itemView.tv_item_title.apply {
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
                itemView.tv_item_due_date.apply {
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
                itemView.tv_due_date.apply {
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
            }else {
                itemView.tv_item_title.apply {
                    paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
                itemView.tv_item_due_date.apply {
                    paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
                itemView.tv_due_date.apply {
                    paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
            }
            if (todo.dueTime!!.toInt() != 0) {
                val dateValues = convertMillis(todo.dueTime)
                val displayFormat: String

                if (dateValues[4]<10) {
                    displayFormat = String.format(
                        itemView.context.getString(R.string.due_date_minute_less_than_ten),
                        convertNumberToMonthName(dateValues[1]),
                        dateValues[0],
                        dateValues[2],
                        dateValues[3],
                        dateValues[4]
                    )
                }else{
                    displayFormat = String
                        .format(
                            itemView.context.getString(R.string.due_date_minute_greater_than_ten),
                            convertNumberToMonthName(dateValues[1]),
                            dateValues[0],
                            dateValues[2],
                            dateValues[3],
                            dateValues[4]
                        )
                }
                itemView.tv_item_due_date.text=displayFormat
            }else{
                itemView.tv_item_due_date.text=itemView.context.getString(R.string.no_due_is_set)
            }


            itemView.checkbox_item.setOnClickListener {
                listener.onCheckClicked(todo)
            }

            itemView.iv_delete_item.setOnClickListener {
                listener.onDeleteClicked(todo)
            }

            itemView.setOnClickListener {
                listener.onViewClicked(todo)
            }
        }
    }

    /**
     * Search Filter implementation
     * */
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val charString = p0.toString()
                filteredTodoList = if (charString.isEmpty()) {
                    todoList
                } else {
                    val filteredList = arrayListOf<TodoRecord>()
                    for (item in todoList) {
                        if (item.note?.toLowerCase(Locale.getDefault())!!.contains(
                                charString.toLowerCase(
                                    Locale.getDefault()
                                )
                            )
                            || item.title.toLowerCase(Locale.getDefault()).contains(
                                charString.toLowerCase(
                                    Locale.getDefault()
                                )
                            )
                            || item.tags?.toLowerCase(Locale.getDefault())!!.contains(
                                charString.toLowerCase(
                                    Locale.getDefault()
                                )
                            )
                        ) {
                            filteredList.add(item)
                        }
                    }
                    filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = filteredTodoList
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                filteredTodoList = p1?.values as List<TodoRecord>
                notifyDataSetChanged()
            }

        }
    }

    /**
     * Activity uses this method to update todoList with the help of LiveData
     * */
    fun setAllTodoItems(todoItems: List<TodoRecord>) {
        this.todoList = todoItems
        this.filteredTodoList = todoItems
        notifyDataSetChanged()
    }

    /**
     * RecycleView touch event callbacks
     * */
    interface TodoEvents {
        fun onDeleteClicked(todoRecord: TodoRecord)
        fun onViewClicked(todoRecord: TodoRecord)
        fun onCheckClicked(todoRecord: TodoRecord)
    }

}