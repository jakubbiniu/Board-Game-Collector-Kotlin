
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
    private lateinit var photoPaths: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        var username = sharedPreferences.getString("username", null)
        if (username == "user") {
            username = null
        }
        if (username.isNullOrEmpty()) {
            val intent = Intent(this, ConfigurationActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            setContentView(R.layout.activity_main)
            val hello: TextView = findViewById(R.id.hello)
            hello.text = "Hello, " + username.toString()

            var synchroDate = sharedPreferences.getString("synchroDate", null)
            val date: TextView = findViewById(R.id.dateSynchro)
            date.text = "Last synchronization date: " + synchroDate.toString()


            dbHelper = DatabaseHelper(this)


            val rowCount = dbHelper.getGamesCount()
            val expansionCount = dbHelper.getExpansionsCount()


            val rowCountTextView: TextView = findViewById(R.id.gamesNumber)
            rowCountTextView.text = "Number of games: $rowCount"

            val expansionCountTextView: TextView = findViewById(R.id.extensionNumber)
            expansionCountTextView.text = "Number of expansions: $expansionCount"


            photoPaths = getPhotoPaths()


            val resetButton: View = findViewById(R.id.reset)
            resetButton.setOnClickListener {
                reset(photoPaths)
            }
        }
    }

    private fun getPhotoPaths(): ArrayList<String> {
        val photoPaths = ArrayList<String>()
        val gameFolders = getExternalFilesDir(null)?.listFiles()?.filter { it.isDirectory }
        gameFolders?.forEach { gameFolder ->
            gameFolder.listFiles()?.forEach { file ->
                if (file.isFile && file.extension.equals("jpg", ignoreCase = true)) {
                    val path = file.absolutePath
                    photoPaths.add(path)
                    println("Photo Path: $path")
                }
            }
        }
        return photoPaths
    }



    private fun reset(photoPaths: ArrayList<String>) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Clear Data")
        dialogBuilder.setMessage("Are you sure you want to clear all data?")
        dialogBuilder.setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
            val externalFilesDir = getExternalFilesDir(null)
            externalFilesDir?.listFiles()?.forEach { gameFolder ->
                if (gameFolder.isDirectory) {
                    gameFolder.deleteRecursively()
                }
            }

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




    fun synchronise(v: View) {
        val intent = Intent(this, SynchronizationActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun viewGames(v: View) {
        val intent = Intent(this, GamesActivity::class.java)
        startActivity(intent)
    }

    fun viewExtensions(v: View) {
        val intent = Intent(this, ExtensionActivity::class.java)
        startActivity(intent)
    }
}
