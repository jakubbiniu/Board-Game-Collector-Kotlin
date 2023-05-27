package edu.put.inf151921

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GamesActivity : AppCompatActivity() {
    private lateinit var gameAdapter: GameAdapter
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_games)
        dbHelper = DatabaseHelper(this)
        // Initialize the RecyclerView and adapter
        val gameList: RecyclerView = findViewById(R.id.gameList)
        gameAdapter = GameAdapter()
        gameList.adapter = gameAdapter
        gameList.layoutManager = LinearLayoutManager(this)

        // Create a list of games and set it to the adapter
        val games = createGameList()
        gameAdapter.setGames(games)
    }

    private fun createGameList(): List<Game> {
        val games = mutableListOf<Game>()

        // Retrieve games from the database using your DatabaseHelper instance
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            DatabaseHelper.COLUMN_NAME,
            DatabaseHelper.COLUMN_DESCRIPTION,
            DatabaseHelper.COLUMN_YEAR_PUBLISHED
        )
        val cursor = db.query(
            DatabaseHelper.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            val game = Game()
            game.name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME))
            game.description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION))
            game.yearPublished = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_YEAR_PUBLISHED))
            games.add(game)
        }

        cursor.close()

        return games
    }

}
