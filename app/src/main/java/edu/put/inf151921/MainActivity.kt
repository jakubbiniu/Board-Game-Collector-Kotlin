package edu.put.inf151921

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import java.security.cert.Extension

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        var username = sharedPreferences.getString("username", null)
        if (username == null) {
            val intent = Intent(this, ConfigurationActivity::class.java)
            startActivity(intent)
            finish()
        }
        else {
            setContentView(R.layout.activity_main)
            val hello: TextView = findViewById(R.id.hello)
            hello.text = "Cześć, " + username.toString()
        }
    }
    fun reset(v: View){
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("username")
        editor.apply()
        finishAffinity()
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