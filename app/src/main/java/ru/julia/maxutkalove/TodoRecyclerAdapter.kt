package ru.julia.maxutkalove

import android.view.*
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_todo.view.*
import ru.julia.maxutkalove.TodoRecyclerAdapter.TodoViewHolder

class TodoRecyclerAdapter(
    private val todos: ArrayList<Todo>,
    private val todoItemListener: TodoItemListener,
) : RecyclerView.Adapter<TodoViewHolder>() {

    inner class TodoViewHolder(view: View)
        : RecyclerView.ViewHolder(view),
        PopupMenu.OnMenuItemClickListener {
         init {
            view.itemTodoMenuBtn.apply {
                setOnClickListener {
                    val popupMenu = PopupMenu(it.context, it)
                    popupMenu.inflate(R.menu.menu_todo)
                    popupMenu.setOnMenuItemClickListener(this@TodoViewHolder)
                    popupMenu.show()
                }
            }
        }
        val title: AppCompatTextView = view.itemTodoTitle
        override fun onMenuItemClick(item: MenuItem): Boolean {
            return when(item.itemId) {
                R.id.todo_edit -> {
                    todoItemListener.onItemEdit(adapterPosition)
                    true
                }
                R.id.todo_delete -> {
                    todoItemListener.onItemDelete(adapterPosition)
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = todos[position]
        holder.title.text = todo.title
    }

    override fun getItemCount(): Int = todos.size

}