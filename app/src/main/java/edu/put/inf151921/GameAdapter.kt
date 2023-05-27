package edu.put.inf151921

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class GameAdapter(context: Context, games: List<Game>) : ArrayAdapter<Game>(context, 0, games) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        val holder: ViewHolder

        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.list_item_game, parent, false)
            holder = ViewHolder(itemView)
            itemView.tag = holder
        } else {
            holder = itemView.tag as ViewHolder
        }

        val game = getItem(position)
        game?.let {
            holder.gameName.text = game.name
            holder.yearPublished.text = game.yearPublished.toString()
            holder.gameDescription.text=game.description
        }

        return itemView!!
    }

    private class ViewHolder(view: View) {
        val gameName: TextView = view.findViewById(R.id.gameName)
        val yearPublished: TextView = view.findViewById(R.id.yearPublished)
        val gameDescription: TextView = view.findViewById(R.id.gameDescription)

    }
}
