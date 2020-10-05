package ru.julia.maxutkalove.ui.todoDetails

import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.noties.markwon.Markwon
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.MarkwonEditorTextWatcher
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.image.data.DataUriSchemeHandler
import io.noties.markwon.image.svg.SvgMediaDecoder
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
        initMarkwon()
    }

    private fun initMarkwon() {
        val markwon: Markwon = Markwon.builder(this)
            .usePlugin(ImagesPlugin.create { plugin ->
                plugin.addSchemeHandler(DataUriSchemeHandler.create())
                plugin.addMediaDecoder(SvgMediaDecoder.create(resources))
            })
            .usePlugin(HtmlPlugin.create())
            .usePlugin(TablePlugin.create(this))
            .usePlugin(TaskListPlugin.create(this))
            .build()
        val editor: MarkwonEditor = MarkwonEditor.create(markwon)
        todoBottom.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(editor))
        todoBottom.addTextChangedListener { todoTop.text = markwon.toMarkdown(todoBottom.text.toString()) }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_todo_top, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.todoSave -> {
                saveTodo()
                toast("Saved")
                true
            }
            R.id.todoEdit -> {
                MaterialAlertDialogBuilder(this).apply {
                    setTitle(getString(R.string.edit_todo_name))
                    val editText = EditText(this@TodoActivity)
                    editText.setText(currentTodo.title)
                    setView(editText)
                    setPositiveButton(android.R.string.ok) { dialog, _ ->
                        val newTitle = editText.text.toString()
                        if (newTitle.isNotBlank()){
                            currentTodo.title = newTitle
                            title = newTitle
                            saveTodo()
                        }
                        dialog.dismiss()
                    }
                    setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss()}
                    show()
                }
                true
            }
            else -> false
        }
    }

    override fun onPause() {
        super.onPause()
        saveTodo()
    }

    private fun saveTodo() {
        currentTodo.body = todoBottom.text.toString()
        DBHelper.updateTodo(todoId, currentTodo.title, currentTodo.body)
    }
}