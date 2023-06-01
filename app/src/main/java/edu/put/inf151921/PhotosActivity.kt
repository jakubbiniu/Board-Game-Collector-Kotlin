package edu.put.inf151921

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PhotosActivity : AppCompatActivity() {

    private lateinit var photoPaths: ArrayList<String>
    private lateinit var photoAdapter: PhotoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photos)

        // Retrieve the photo paths from the intent
        photoPaths = intent.getStringArrayListExtra("photoPaths") ?: ArrayList()

        // Initialize the RecyclerView and adapter
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        photoAdapter = PhotoAdapter(photoPaths)
        recyclerView.adapter = photoAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

}