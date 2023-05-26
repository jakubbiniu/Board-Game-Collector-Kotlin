package edu.put.inf151921

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class GameAdapter : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {
    private var games: List<Game> = emptyList()

    fun setGames(games: List<Game>) {
        this.games = games
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_game, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = games[position]
        holder.bind(game)
    }

    override fun getItemCount(): Int {
        return games.size
    }

    inner class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val gameName: TextView = itemView.findViewById(R.id.gameName)

        fun bind(game: Game) {
            gameName.text = game.name
        }
    }
}


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

        // Retrieve games from the database using DatabaseHelper
        val dbHelper = DatabaseHelper(this)
        val games = dbHelper.getAllGames() // Implement getAllGames() method in DatabaseHelper

        gameAdapter.setGames(games)
    }

}
