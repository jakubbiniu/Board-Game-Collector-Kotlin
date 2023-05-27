package edu.put.inf151921

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog


class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        var username = sharedPreferences.getString("username", null)
        if (username == null) {
            val intent = Intent(this, ConfigurationActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            setContentView(R.layout.activity_main)
            val hello: TextView = findViewById(R.id.hello)
            hello.text = "Hello, " + username.toString()

            // Initialize DatabaseHelper
            dbHelper = DatabaseHelper(this)

            // Get the number of rows in the database
            val rowCount = dbHelper.getGamesCount()

            // Update the TextView with the number of rows
            val rowCountTextView: TextView = findViewById(R.id.gamesNumber)
            rowCountTextView.text = "Number of games: $rowCount"
        }
    }
    fun reset(v: View){
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Clear Data")
        dialogBuilder.setMessage("Are you sure you want to clear all data?")
        dialogBuilder.setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
            val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("username")
            editor.apply()
            finishAffinity()
        }
        dialogBuilder.setNegativeButton("No") { dialogInterface: DialogInterface, i: Int ->
        }
        dialogBuilder.show()
    }

    fun synchronise(v: View){
        val intent = Intent(this, SynchronizationActivity::class.java)
        startActivity(intent)
    }
    fun viewGames(v: View){
        val intent = Intent(this, GamesActivity::class.java)
        startActivity(intent)
    }
    fun viewExtensions(v: View){
        val intent = Intent(this, ExtensionActivity::class.java)
        startActivity(intent)
    }

}