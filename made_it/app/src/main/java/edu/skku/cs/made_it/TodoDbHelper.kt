package edu.skku.cs.made_it

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TodoDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "todo.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NAME = "todo"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_MEMO = "memo"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_CHECKED = "checked"

    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID TEXT PRIMARY KEY, $COLUMN_TITLE TEXT, $COLUMN_MEMO TEXT, $COLUMN_DATE TEXT, $COLUMN_CHECKED INT)"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun createTodo(todo: Todo) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID, todo.id)
            put(COLUMN_TITLE, todo.title)
            put(COLUMN_MEMO, todo.memo)
            put(COLUMN_DATE, todo.date)
            put(COLUMN_CHECKED, todo.checked)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getTodoById(id: Int): Todo? {
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_NAME,
            null,
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        val schedule: Todo? = if (cursor.moveToFirst()) {
            val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
            val description = cursor.getString(cursor.getColumnIndex(COLUMN_MEMO))
            val date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE))
            val checked = cursor.getInt(cursor.getColumnIndex(COLUMN_CHECKED))
            Todo(id, title, description, date, checked)
        } else {
            null
        }
        cursor.close()
        db.close()
        return schedule
    }

    fun updateTodo(todo: Todo) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, todo.title)
            put(COLUMN_MEMO, todo.memo)
            put(COLUMN_DATE, todo.date)
            put(COLUMN_CHECKED, todo.checked)
        }
        db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(todo.id.toString()))
        db.close()
    }

    fun deleteTodo(id: String) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id))
        db.close()
    }

    fun getLatestId(): Int {
        val db = readableDatabase
        val query = "SELECT MAX($COLUMN_ID) FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)
        val latestId = if (cursor.moveToFirst()) {
            cursor.getInt(0)
        } else {
            1
        }
        cursor.close()
        db.close()
        return latestId
    }
}
