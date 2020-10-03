package ru.julia.maxutkalove.ui.todoDetails

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_todo.*
import ru.julia.maxutkalove.R
import ru.julia.maxutkalove.model.Todo
import ru.julia.maxutkalove.repository.DBHelper
import ru.julia.maxutkalove.util.Extensions.toast

class TodoActivity : AppCompatActivity() {

    private var todoId: Long = -1
    private lateinit var currentTodo: Todo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        todoId = intent.getLongExtra("todoId", -1)
        if (todoId < 0) {
            toast("Error")
            finish()
        }
        val todo = DBHelper.getTodoById(todoId)
        if (todo != null) currentTodo = todo
        else {
            toast("Error")
            finish()
        }
        title = currentTodo.title
        todoTop.text = currentTodo.body
        todoBottom.setText(currentTodo.body)
    }
}