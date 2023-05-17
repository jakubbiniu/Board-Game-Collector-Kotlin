package edu.put.inf151921

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText

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
        }
    }
    fun reset(v: View){
        val sharedPreferences = getSharedPreferences("your_preference_name", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", null)
        editor.apply()
        finishAffinity()
    }
}