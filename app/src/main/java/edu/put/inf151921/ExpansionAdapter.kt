package edu.put.inf151921

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


class ExpansionAdapter(context: Context, games: List<Game>) : ArrayAdapter<Game>(context, 0, games) {
    var x:Int = 1
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
            holder.gameId.text = x.toString()
            x+=1
            holder.gameName.text = game.name
            holder.yearPublished.text = game.yearPublished.toString()
            //holder.gameGameId.text= game.gameId.toString()
        }

        return itemView!!
    }

    private class ViewHolder(view: View) {
        val gameId:TextView = view.findViewById(R.id.gameId)
        val gameName: TextView = view.findViewById(R.id.gameName)
        val yearPublished: TextView = view.findViewById(R.id.yearPublished)
        //val gameGameId: TextView = view.findViewById(R.id.gameGameId)

    }
}
