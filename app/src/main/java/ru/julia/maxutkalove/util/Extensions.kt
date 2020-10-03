package ru.julia.maxutkalove.util

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

object Extensions {

    fun Context.toast(text: CharSequence) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    fun Context.toast(@StringRes stringResId: Int) = Toast.makeText(this, getString(stringResId), Toast.LENGTH_SHORT).show()
}