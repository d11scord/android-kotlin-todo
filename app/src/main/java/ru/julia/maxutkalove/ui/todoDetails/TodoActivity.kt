package ru.julia.maxutkalove.ui.todoDetails

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
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

class TodoActivity : AppCompatActivity(), View.OnClickListener {

    private var todoId: Long = -1
    private lateinit var currentTodo: Todo
    private val shortcuts = ArrayList<MDShortcut>()

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
        todoBottom.setText(currentTodo.body)
        initMarkwon()
        initShortcuts()
    }

    private fun initShortcuts() {
        val h1 = MDShortcut.H1(View.generateViewId())
        val h2 = MDShortcut.H2(View.generateViewId())
        val h3 = MDShortcut.H3(View.generateViewId())
        val h4 = MDShortcut.H4(View.generateViewId())
        val h5 = MDShortcut.H5(View.generateViewId())
        val h6 = MDShortcut.H6(View.generateViewId())
        val bold = MDShortcut.Bold(View.generateViewId())
        val italic = MDShortcut.Italic(View.generateViewId())
        val listItem = MDShortcut.ListItem(View.generateViewId())
        shortcuts.add(h1)
        shortcuts.add(h2)
        shortcuts.add(h3)
        shortcuts.add(h4)
        shortcuts.add(h5)
        shortcuts.add(h6)
        shortcuts.add(bold)
        shortcuts.add(italic)
        shortcuts.add(listItem)
        shortcuts.forEach {
            val button = Button(this)
            button.text = it.name.trim()
            button.id = it.buttonResId
            button.setOnClickListener(this)
            todoShortcutsHost.addView(button)
        }
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
        todoTop.text = markwon.toMarkdown(currentTodo.body)
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
                        if (newTitle.isNotBlank()) {
                            currentTodo.title = newTitle
                            title = newTitle
                            saveTodo()
                        }
                        dialog.dismiss()
                    }
                    setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
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

    override fun onClick(v: View) {
        val shortcut = shortcuts.find { it.buttonResId == v.id }
        if (shortcut != null) {
            val start = todoBottom.selectionStart.coerceAtLeast(0)
            val end = todoBottom.selectionEnd.coerceAtLeast(0)
            todoBottom.text?.replace(
                start.coerceAtMost(end),
                start.coerceAtLeast(end),
                shortcut.shortcut,
                0,
                shortcut.shortcut.length
            )
            val selectionStart = todoBottom.selectionStart - (shortcut.shortcut.length - shortcut.cursorPosition)
            todoBottom.setSelection(selectionStart, selectionStart + shortcut.selectionLength)
        }
    }
}