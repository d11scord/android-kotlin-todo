package ru.julia.maxutkalove

import android.view.*
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_todo.view.*
import ru.julia.maxutkalove.TodoRecyclerAdapter.TodoViewHolder

class TodoRecyclerAdapter(
    private val todos: ArrayList<Todo>,
    private val onItemClick: (position: Int) -> Unit
) : RecyclerView.Adapter<TodoViewHolder>() {

    inner class TodoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: AppCompatTextView = view.itemTodoTitle
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = todos[position]
        holder.title.text = todo.title
        holder.itemView.setOnClickListener { onItemClick(position) }
    }

    override fun getItemCount(): Int = todos.size
}