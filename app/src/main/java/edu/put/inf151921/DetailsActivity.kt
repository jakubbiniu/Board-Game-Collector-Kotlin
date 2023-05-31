package edu.put.inf151921

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso


class DetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val image = intent.getStringExtra("image")
        val name = intent.getStringExtra("name")
        val description = intent.getStringExtra("description")
        val min = intent.getIntExtra("min", 0)
        val max = intent.getIntExtra("max", 0)
        val year = intent.getIntExtra("year", 0)
        val rank = intent.getIntExtra("rank",0)

        val picture:ImageView=findViewById(R.id.imageView)
        val title:TextView = findViewById(R.id.titleTextView)
        title.text = name
        val desc:TextView = findViewById(R.id.descriptionTextView)
        desc.text = "Description:\n" +
                Html.fromHtml(description)
        val players:TextView = findViewById(R.id.playersTextView)
        players.text = "Minimum number of players: $min\nMaximum number of players: $max"
        val rankText:TextView = findViewById(R.id.rankTextView)
        rankText.text = "Position in boardgames rank: $rank"
        val yearText:TextView = findViewById(R.id.yearTextView)
        yearText.text = "Release year: $year"
        Picasso.get()
            .load(image)
            .placeholder(R.drawable.placeholder_image)
            //.error(R.drawable.error_image)
            .into(picture)



    }
}