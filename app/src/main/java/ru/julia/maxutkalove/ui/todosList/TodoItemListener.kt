package ru.julia.maxutkalove.ui.todosList

interface TodoItemListener {
    fun onItemEdit(pos: Int)
    fun onItemDelete(pos: Int)
}