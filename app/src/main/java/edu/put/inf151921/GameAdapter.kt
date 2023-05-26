package edu.put.inf151921

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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
        private val gameImage: ImageView = itemView.findViewById(R.id.gameImage)
        private val gameName: TextView = itemView.findViewById(R.id.gameName)
        private val gameDescription: TextView = itemView.findViewById(R.id.gameDescription)
        private val gameYearPublished: TextView = itemView.findViewById(R.id.gameYearPublished)

        fun bind(game: Game) {
            // Set the values of the views using the game object
            gameImage.setImageResource(game.imageResId)
            gameName.text = game.name
            gameDescription.text = game.description
            gameYearPublished.text = "Year Published: ${game.yearPublished}"
        }
    }
}
