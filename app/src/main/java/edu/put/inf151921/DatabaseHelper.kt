package edu.put.inf151921

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "boardgames.db"
        private const val DATABASE_VERSION = 1
        // Define your table name and columns here
        const val TABLE_NAME = "games"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "username"
        const val COLUMN_ICON = "icon"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NAME TEXT, $COLUMN_ICON IMAGE)"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
//         Handle database upgrades if needed
    }
    fun getAllGames(): List<Game> {
        val games: MutableList<Game> = mutableListOf()

        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val game = Game()

            val idIndex = cursor.getColumnIndex(COLUMN_ID)
            if (idIndex != -1) {
                game.id = cursor.getInt(idIndex)
            }

            val nameIndex = cursor.getColumnIndex(COLUMN_NAME)
            if (nameIndex != -1) {
                game.name = cursor.getString(nameIndex)
            }

            games.add(game)
        }

        cursor.close()
        db.close()

        return games
    }




}