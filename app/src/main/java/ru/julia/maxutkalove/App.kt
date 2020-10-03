package ru.julia.maxutkalove

import android.app.Application
import ru.julia.maxutkalove.repository.DBHelper

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        DBHelper.context = applicationContext
    }
}