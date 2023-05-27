package edu.put.inf151921

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "boardgames.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "games"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_YEAR_PUBLISHED = "year_published"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        val createTableQuery = "CREATE TABLE $TABLE_NAME " +
                "($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_NAME TEXT, $COLUMN_YEAR_PUBLISHED INTEGER, $COLUMN_DESCRIPTION TEXT)"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun getGamesCount(): Int {
        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery("SELECT COUNT(*) FROM $TABLE_NAME", null)
        var count = 0
        cursor?.let {
            if (it.moveToFirst()) {
                count = cursor.getInt(0)
            }
            cursor.close()
        }
        db.close()
        return count
    }

    fun getAllGames(): List<Game> {
        val gamesList = mutableListOf<Game>()

        val selectQuery = "SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_NAME"
        val cursor = readableDatabase.rawQuery(selectQuery, null)

        cursor.use {
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                val yearPublished = cursor.getInt(cursor.getColumnIndex(COLUMN_YEAR_PUBLISHED))
                val description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION))
                val game = Game()
                game.id = id
                game.name = name
                game.yearPublished = yearPublished
                game.description = description
                gamesList.add(game)
            }
        }

        return gamesList
    }

}

