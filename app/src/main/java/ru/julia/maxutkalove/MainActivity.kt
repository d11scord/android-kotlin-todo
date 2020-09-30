package ru.julia.maxutkalove

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import ru.julia.maxutkalove.Extensions.toast

class MainActivity : AppCompatActivity(), TodoItemListener {

    private val todos = ArrayList<Todo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        todos.add(Todo(1, "title", "text"))
        todos.add(Todo(2, "title1", "text1"))
        todosRecycler.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = TodoRecyclerAdapter(todos, this@MainActivity)
        }
    }

    override fun onItemEdit(pos: Int) {
        val intent = Intent(this, TodoActivity::class.java)
        intent.putExtra("todoId", todos[pos].id)
        startActivity(intent)
    }

    override fun onItemDelete(pos: Int) {
        todos.removeAt(pos)
        todosRecycler.adapter?.notifyItemRemoved(pos)
    }

}