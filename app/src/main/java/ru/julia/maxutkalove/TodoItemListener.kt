package ru.julia.maxutkalove

interface TodoItemListener {
    fun onItemEdit(pos: Int)
    fun onItemDelete(pos: Int)
}