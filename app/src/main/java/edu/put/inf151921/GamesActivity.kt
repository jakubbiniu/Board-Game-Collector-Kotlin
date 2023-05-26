package edu.put.inf151921

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.put.inf151921.GameAdapter.GameViewHolder

class GamesActivity : AppCompatActivity() {
    private lateinit var gameList: RecyclerView
    private lateinit var gameAdapter: GameAdapter
    private lateinit var gameCollection: GameCollection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_games)

        gameList = findViewById(R.id.gameList)
        gameList.layoutManager = LinearLayoutManager(this)
        gameAdapter = GameAdapter()
        gameList.adapter = gameAdapter

        gameCollection = GameCollection()
        val games = gameCollection.getGamesList()
        gameAdapter.setGames(games)
    }
}
