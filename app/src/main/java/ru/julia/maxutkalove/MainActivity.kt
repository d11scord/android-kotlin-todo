package ru.julia.maxutkalove

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import ru.julia.maxutkalove.Extensions.toast

class MainActivity : AppCompatActivity(), TodoItemListener {

    private val todos = ArrayList<Todo>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        todos.add(Todo(1, "title", "text"))
        todos.add(Todo(2, "title1", "text1"))
        val swipeBackground = ColorDrawable(ContextCompat.getColor(this, R.color.swipe_red))
        val deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete)!!
        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, LEFT or RIGHT) {
            override fun onMove(
                p0: RecyclerView,
                p1: RecyclerView.ViewHolder,
                p2: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                todos.removeAt(viewHolder.adapterPosition)
                todosRecycler.adapter?.notifyItemRemoved(viewHolder.adapterPosition)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                val itemView = viewHolder.itemView
                val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2
                if (dX > 0) {
                    swipeBackground.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                    deleteIcon.setBounds(
                        itemView.left + iconMargin,
                        itemView.top + iconMargin,
                        itemView.left + iconMargin + deleteIcon.intrinsicWidth,
                        itemView.bottom - iconMargin
                    )
                } else {
                    swipeBackground.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
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
                    c.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                }
                deleteIcon.draw(c)
                c.restore()
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        todosRecycler.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = TodoRecyclerAdapter(todos, this@MainActivity)
            itemTouchHelper.attachToRecyclerView(this)
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