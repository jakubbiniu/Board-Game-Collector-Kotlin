package edu.put.inf151921


import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity


class GamesActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_games)

        listView = findViewById(R.id.listView)
        databaseHelper = DatabaseHelper(this)

        val gamesList = databaseHelper.getAllGames()

        val adapter = GameAdapter(this, gamesList)
        listView.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        databaseHelper.close()
    }


}
