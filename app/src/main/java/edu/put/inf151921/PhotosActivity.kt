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

        // Retrieve the game ID from the intent
        val gameId = intent.getLongExtra("gameId", 0)

        // Retrieve the photo paths for the specific game ID
        photoPaths = getPhotoPathsForGame(gameId)

        // Initialize the RecyclerView and adapter
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        photoAdapter = PhotoAdapter(photoPaths)
        recyclerView.adapter = photoAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun getPhotoPathsForGame(gameId: Long): ArrayList<String> {
        val photoPaths = ArrayList<String>()
        val gameFolder = getExternalFilesDir(null)?.resolve(gameId.toString())

        gameFolder?.let { folder ->
            if (folder.exists() && folder.isDirectory) {
                folder.listFiles()?.forEach { file ->
                    if (file.isFile && file.extension.equals("jpg", ignoreCase = true)) {
                        photoPaths.add(file.absolutePath)
                    }
                }
            }
        }

        return photoPaths
    }
}
