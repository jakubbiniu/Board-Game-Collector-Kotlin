package edu.put.inf151921


import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class PhotosActivity : AppCompatActivity() {

    private lateinit var photoPaths: ArrayList<String>
    private lateinit var photoAdapter: PhotoAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photos)


        val gameId = intent.getLongExtra("gameId", 0)


        photoPaths = getPhotoPathsForGame(gameId)


        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

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
        dialogBuilder.setTitle("Delete a photo")
        dialogBuilder.setMessage("Are you sure you want to delete that photo?")
        dialogBuilder.setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
            val photoPath = photoPaths[position]


            val file = File(photoPath)
            if (file.exists() && file.isFile) {
                if (file.delete()) {

                    photoPaths.removeAt(position)
                    photoAdapter.notifyItemRemoved(position)
                } else {

                    Toast.makeText(this, "Failed to delete the photo", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialogBuilder.setNegativeButton("No") { dialogInterface: DialogInterface, i: Int ->
        }
        dialogBuilder.show()

    }
}
