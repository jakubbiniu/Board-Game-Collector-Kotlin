package edu.put.inf151921


import android.content.Context
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity


class ExtensionActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var databaseHelper: DatabaseHelper



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_extension)


        listView = findViewById(R.id.listView)
        databaseHelper = DatabaseHelper(this)

        val gamesList = databaseHelper.getAllExpansions()

        val adapter = ExpansionAdapter(this, gamesList)
        listView.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        databaseHelper.close()
    }


}
