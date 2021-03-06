package ru.julia.maxutkalove.ui.todosList

import android.content.Intent
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_todos_list.*
import ru.julia.maxutkalove.R
import ru.julia.maxutkalove.model.Todo
import ru.julia.maxutkalove.repository.DBHelper
import ru.julia.maxutkalove.ui.todoDetails.TodoActivity
import ru.julia.maxutkalove.util.Extensions.toast

class TodosListActivity : AppCompatActivity() {

    private val todos = ArrayList<Todo>()
    private var lastDeletedTodo: Todo? = null
    private var lastDeletedItemPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todos_list)
        updateTodosList()
        todosListRefreshLayout.setOnRefreshListener { updateTodosList() }
        addTodoBtn.setOnClickListener {
            MaterialAlertDialogBuilder(this).apply {
                setTitle(getString(R.string.create_new_todo))
                val editText = EditText(this@TodosListActivity)
                setView(editText)
                setPositiveButton(android.R.string.ok) { dialog, _ ->
                    if (!editText.text.isNullOrBlank()){
                        onItemCreate(editText.text.toString())
                    }
                    dialog.dismiss()
                }
                setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss()}
                show()
            }
        }

        val swipeBackground = ColorDrawable(ContextCompat.getColor(this, R.color.swipe_red))
        val deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete)!!
        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, LEFT or RIGHT) {

            override fun onMove(
                p0: RecyclerView,
                p1: RecyclerView.ViewHolder,
                p2: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) =
                onItemDelete(viewHolder.adapterPosition)

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                val itemView = viewHolder.itemView
                val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2
                if (dX > 0) {
                    swipeBackground.setBounds(
                        itemView.left,
                        itemView.top,
                        dX.toInt(),
                        itemView.bottom
                    )
                    deleteIcon.setBounds(
                        itemView.left + iconMargin,
                        itemView.top + iconMargin,
                        itemView.left + iconMargin + deleteIcon.intrinsicWidth,
                        itemView.bottom - iconMargin
                    )
                } else {
                    swipeBackground.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    deleteIcon.setBounds(
                        itemView.right - iconMargin - deleteIcon.intrinsicWidth,
                        itemView.top + iconMargin,
                        itemView.right - iconMargin,
                        itemView.bottom - iconMargin
                    )
                }
                c.save()

                swipeBackground.draw(c)
                if (dX > 0) {
                    c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                } else {
                    c.clipRect(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                }
                deleteIcon.draw(c)
                c.restore()
            }
        }
        ItemTouchHelper(itemTouchCallback).attachToRecyclerView(todosRecycler)
    }

    private fun updateTodosList() {
        todos.clear()
        todos.addAll(DBHelper.getTodos())
        todosRecycler.apply {
            layoutManager = LinearLayoutManager(this@TodosListActivity)
            adapter = TodoRecyclerAdapter(todos) { position: Int -> // onItemClick
                onItemClick(position)
            }
        }
        todosListRefreshLayout.isRefreshing = false
    }

    private fun onItemClick(pos: Int) {
        val intent = Intent(this, TodoActivity::class.java)
        intent.putExtra("todoId", todos[pos].id)
        startActivity(intent)
    }

    private fun onItemCreate(title: String) {
        val newTodoId = DBHelper.createTodo(title)
        if (newTodoId == -1L) {
            toast(R.string.an_error_occurred_while_creating_todo)
            return
        }
        val intent = Intent(this, TodoActivity::class.java)
        intent.putExtra("todoId", newTodoId)
        startActivity(intent)
    }

    private fun onItemDelete(pos: Int) {
        val deletedTodoId = todos[pos].id
        val deletionDelayThread = Thread {
            try {
                Thread.sleep(1500)
                DBHelper.deleteById(deletedTodoId) }
            catch (ignored: InterruptedException) {}
        }.also { it.start() }

        lastDeletedItemPosition = pos
        lastDeletedTodo = todos[pos].copy()
        todos.removeAt(pos)

        todosRecycler.adapter?.notifyItemRemoved(pos)
        Snackbar.make(
            findViewById(android.R.id.content),
            "Todo \"${lastDeletedTodo?.title}\" deleted",
            Snackbar.LENGTH_SHORT
        ).apply {
            setAction(R.string.undo) {
                    if (lastDeletedItemPosition >= 0 && lastDeletedTodo != null) {
                        deletionDelayThread.interrupt()
                        todos.add(lastDeletedItemPosition, lastDeletedTodo!!)
                        todosRecycler.adapter?.notifyItemInserted(lastDeletedItemPosition)
                        dismiss()
                    }
                }
            show()
        }
    }
}