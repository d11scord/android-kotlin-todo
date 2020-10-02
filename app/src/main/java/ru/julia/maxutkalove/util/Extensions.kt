package ru.julia.maxutkalove.util

import android.content.Context
import android.widget.Toast

object Extensions {

    fun Context.toast(text: CharSequence) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}