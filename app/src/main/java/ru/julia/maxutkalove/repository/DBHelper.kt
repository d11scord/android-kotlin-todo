package ru.julia.maxutkalove.repository

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import ru.julia.maxutkalove.model.Todo
import java.lang.Exception

/**
 * Singleton class for managing application database
 * Contains methods to access database data
 * Recommended to implement data access functions in this class only
 *
 * Uses @code applicationContext provided from [ru.julia.maxutkalove.App]
 */
object DBHelper {

    lateinit var context: Context
    private const val DB_NAME = "database"
    private const val DB_VERSION = 1

    private const val TODOS_TABLE_NAME = "todos"
        private const val TODOS_ID_NAME = "id"
        private const val TODOS_TITLE_NAME = "title"
        private const val TODOS_BODY_NAME = "body"

    private val dbHelper: SQLiteHelper by lazy { SQLiteHelper(context) }

    fun getTodos(): ArrayList<Todo> {
        val readableDatabase = dbHelper.readableDatabase
        val cursor = readableDatabase.query(TODOS_TABLE_NAME, null, null, null, null, null, null)
        val idIndex = cursor.getColumnIndex(TODOS_ID_NAME)
        val titleIndex = cursor.getColumnIndex(TODOS_TITLE_NAME)
        val bodyIndex = cursor.getColumnIndex(TODOS_BODY_NAME)

        val todos = ArrayList<Todo>()
        if (cursor.count <= 0) return todos

        try {
            if (cursor.moveToFirst()){
                do {
                    val id = cursor.getLong(idIndex)
                    val title = cursor.getString(titleIndex)
                    val body = cursor.getString(bodyIndex)
                    todos.add(Todo(
                        id,
                        title,
                        body
                    ))
                } while (cursor.moveToNext())
            }
        } catch (ignored: Exception) {

        } finally {
            cursor.close()
            return todos
        }
    }

    fun getTodoById(todoId: Long): Todo? {
        val cursor =
            dbHelper
                .readableDatabase
                .rawQuery("SELECT * FROM $TODOS_TABLE_NAME WHERE $TODOS_ID_NAME=$todoId", null)
        try {
            if (cursor.moveToFirst()){
                val idIndex = cursor.getColumnIndex(TODOS_ID_NAME)
                val titleIndex = cursor.getColumnIndex(TODOS_TITLE_NAME)
                val bodyIndex = cursor.getColumnIndex(TODOS_BODY_NAME)

                val id = cursor.getLong(idIndex)
                val title = cursor.getString(titleIndex)
                val body = cursor.getString(bodyIndex)
                return Todo(id, title, body)
            }
        } catch (ignored: Exception) {

        } finally {
            cursor.close()
        }
        return null
    }

    fun createTodo(title: String): Long {
        val cv = ContentValues()
        cv.put(TODOS_TITLE_NAME, title)
        cv.put(TODOS_BODY_NAME, "")
        return dbHelper.writableDatabase.insert(TODOS_TABLE_NAME, null, cv)
    }

    class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE $TODOS_TABLE_NAME (
                $TODOS_ID_NAME INTEGER PRIMARY KEY AUTOINCREMENT,
                $TODOS_TITLE_NAME TEXT,
                $TODOS_BODY_NAME TEXT
                );
            """.trimIndent()
            )
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
    }
}
