package edu.put.inf151921

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "boardgames.db"
        private const val DATABASE_VERSION = 1
        // Define your table name and columns here
        const val TABLE_NAME = "games"
        private const val COLUMN_ID = "id"
        const val COLUMN_NAME = "username"
        const val COLUMN_ICON = "icon"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create your table here
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NAME TEXT, $COLUMN_ICON IMAGE)"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades if needed
    }

}

class ConfigurationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)
    }
    private fun showInfoMessage() {
        val message = "Database has been successfully created"

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun confirm(v: View){

        val input:EditText = findViewById(R.id.username)
        val username:String = input.text.toString()

        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.apply()

        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.writableDatabase
        val values = ContentValues()
        values.put(DatabaseHelper.COLUMN_NAME, username)
//        values.put(DatabaseHelper.COLUMN_ICON, icon)
        db.insert(DatabaseHelper.TABLE_NAME, null, values)
        db.close()

        showInfoMessage()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}



