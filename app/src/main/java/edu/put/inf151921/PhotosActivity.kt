package edu.put.inf151921

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class PhotosActivity : AppCompatActivity() {

    private lateinit var photoPaths: ArrayList<String>
    private lateinit var photoAdapter: PhotoAdapter

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_photos)
//
//        // Retrieve the game ID from the intent
//        val gameId = intent.getLongExtra("gameId", 0)
//
//        // Retrieve the photo paths for the specific game ID
//        photoPaths = getPhotoPathsForGame(gameId)
//
//        // Initialize the RecyclerView and adapter
//        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
//        photoAdapter = PhotoAdapter(photoPaths)
//        recyclerView.adapter = photoAdapter
//        recyclerView.layoutManager = LinearLayoutManager(this)
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photos)

        // Retrieve the game ID from the intent
        val gameId = intent.getLongExtra("gameId", 0)

        // Retrieve the photo paths for the specific game ID
        photoPaths = getPhotoPathsForGame(gameId)

        // Initialize the RecyclerView and adapter
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        //photoAdapter = PhotoAdapter(photoPaths)
        photoAdapter = PhotoAdapter(photoPaths) { position ->
            deletePhoto(position)
        }
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
    private fun deletePhoto(position: Int) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Clear Data")
        dialogBuilder.setMessage("Are you sure you want to delete that photo?")
        dialogBuilder.setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
            val photoPath = photoPaths[position]

            // Delete the file from the storage
            val file = File(photoPath)
            if (file.exists() && file.isFile) {
                if (file.delete()) {
                    // Remove the item from the list and update the adapter
                    photoPaths.removeAt(position)
                    photoAdapter.notifyItemRemoved(position)
                } else {
                    // Failed to delete the file, show an error message
                    Toast.makeText(this, "Failed to delete the photo", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialogBuilder.setNegativeButton("No") { dialogInterface: DialogInterface, i: Int ->
        }
        dialogBuilder.show()

    }
}
