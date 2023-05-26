package edu.put.inf151921

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "boardgames.db"
        private const val DATABASE_VERSION = 1
        // Define your table name and columns here
        const val TABLE_NAME = "games"
        private const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_THUMBNAIL = "thumbnail"
        const val COLUMN_IMAGE = "image"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_YEAR_PUBLISHED = "year_published"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NAME TEXT, " +
                "$COLUMN_THUMBNAIL TEXT, $COLUMN_IMAGE TEXT, $COLUMN_DESCRIPTION TEXT, $COLUMN_YEAR_PUBLISHED INTEGER)"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades if needed
    }
}
