package ru.julia.maxutkalove.ui.todoDetails

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.julia.maxutkalove.R
import ru.julia.maxutkalove.util.Extensions.toast

class TodoActivity : AppCompatActivity() {

    private var todoId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        todoId = intent.getIntExtra("todoId", -1)
        if (todoId <= 0) {
            toast("Error")
            finish()
        }
        toast("todoId is $todoId")
    }
}