package edu.put.inf151921

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso


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
            holder.gameId.text = (position+1).toString()

            holder.gameName.text = game.name
            if(game.yearPublished>0){
                holder.yearPublished.text = game.yearPublished.toString()
            }
            else{
                holder.yearPublished.text = "-"
            }
            Picasso.get()
                .load(game.thumbnail)
                .placeholder(R.drawable.placeholder_image)

                .into(holder.imageThumbnail)
            if (itemView != null) {
                itemView.setOnClickListener(){
                    val intent = Intent(context, DetailsActivity::class.java)
                    intent.putExtra("image",game.image)
                    intent.putExtra("name",game.name)
                    intent.putExtra("description",game.description)
                    intent.putExtra("min",game.min)
                    intent.putExtra("max",game.max)
                    intent.putExtra("year",game.yearPublished)
                    intent.putExtra("rank",game.rank)
                    intent.putExtra("gameId",game.gameId)
                    context.startActivity(intent)
                }
            }
        }

        return itemView!!
    }

    private class ViewHolder(view: View) {
        val gameId:TextView = view.findViewById(R.id.gameId)
        val gameName: TextView = view.findViewById(R.id.gameName)
        val yearPublished: TextView = view.findViewById(R.id.yearPublished)
        val imageThumbnail: ImageView = view.findViewById(R.id.imageThumbnail)


    }
}
