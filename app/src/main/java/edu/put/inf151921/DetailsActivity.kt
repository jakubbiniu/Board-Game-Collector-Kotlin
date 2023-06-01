//package edu.put.inf151921
//
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.text.Html
//import android.widget.ImageView
//import android.widget.TextView
//import com.squareup.picasso.Picasso
//import android.Manifest
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.net.Uri
//import android.os.Build
//import android.provider.MediaStore
//import android.view.View
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.core.content.FileProvider
//import java.io.File
//import java.io.IOException
//import java.text.SimpleDateFormat
//import java.util.*
//
//
//class DetailsActivity : AppCompatActivity() {
//    private lateinit var currentPhotoPath: String
//    private lateinit var gamePhotoPaths: ArrayList<String>
//    private lateinit var photoAdapter: PhotoAdapter
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_details)
//
//        gamePhotoPaths = ArrayList()
//
//        val image = intent.getStringExtra("image")
//        val name = intent.getStringExtra("name")
//        val description = intent.getStringExtra("description")
//        val min = intent.getIntExtra("min", 0)
//        val max = intent.getIntExtra("max", 0)
//        val year = intent.getIntExtra("year", 0)
//        val rank = intent.getIntExtra("rank",0)
//        val gameId = intent.getLongExtra("gameId",0)
//
//        val picture:ImageView=findViewById(R.id.imageView)
//        val title:TextView = findViewById(R.id.titleTextView)
//        title.text = name
//        val desc:TextView = findViewById(R.id.descriptionTextView)
//        desc.text = "Description:\n" +
//                Html.fromHtml(description)
//        val players:TextView = findViewById(R.id.playersTextView)
//        if (min==0 || max==0){
//            players.text = "No information about number of players"
//        }
//        else{
//            players.text = "Minimum number of players: $min\nMaximum number of players: $max"
//        }
//
//        val rankText:TextView = findViewById(R.id.rankTextView)
//        if (rank==0){
//            rankText.text = "No information about rank"
//        }
//        else{
//            rankText.text = "Position in boardgames rank: $rank"
//        }
//
//        val yearText:TextView = findViewById(R.id.yearTextView)
//        if(year==0){
//            yearText.text = "No information about year"
//        }
//        else{
//            yearText.text = "Release year: $year"
//        }
//
//        Picasso.get()
//            .load(image)
//            .placeholder(R.drawable.placeholder_image)
//            //.error(R.drawable.error_image)
//            .into(picture)
//        photoAdapter = PhotoAdapter(gamePhotoPaths)
//    }
//
//
//    fun seePhotos(view: View) {
//        // Start a new activity or dialog to display the list of photos
//        val intent = Intent(this, PhotosActivity::class.java)
//        intent.putStringArrayListExtra("photoPaths", gamePhotoPaths)
//        startActivity(intent)
//    }
//
//
//    fun takePhoto(view: View) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkCameraPermission()) {
//            requestCameraPermission()
//        } else {
//            dispatchTakePictureIntent()
//        }
//        //gamePhotoPaths.add(currentPhotoPath)
//    }
//
//    fun addPhoto(view: View) {
//        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
//    }
//
//    private fun createImageFile(): File {
//        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//        val storageDir: File? = getExternalFilesDir(null)
//        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
//            currentPhotoPath = absolutePath
//        }
//    }
//
//    private fun dispatchTakePictureIntent() {
//        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//            takePictureIntent.resolveActivity(packageManager)?.also {
//                val photoFile: File? = try {
//                    createImageFile()
//                } catch (ex: IOException) {
//                    ex.printStackTrace()
//                    null
//                }
//                photoFile?.also {
//                    val photoUri: Uri = FileProvider.getUriForFile(
//                        this,
//                        "edu.put.inf151921.fileprovider",
//                        it
//                    )
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
//                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
//                }
//            }
//        }
//    }
//
//    // Helper function to get the real file path from a content URI
//    private fun getRealPathFromUri(uri: Uri): String? {
//        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
//        val cursor = contentResolver.query(uri, filePathColumn, null, null, null)
//        cursor?.moveToFirst()
//        val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
//        val filePath = cursor?.getString(columnIndex!!)
//        cursor?.close()
//        return filePath
//    }
//
//
//    private fun checkCameraPermission(): Boolean {
//        return ContextCompat.checkSelfPermission(
//            this,
//            Manifest.permission.CAMERA
//        ) == PackageManager.PERMISSION_GRANTED
//    }
//
//    private fun requestCameraPermission() {
//        ActivityCompat.requestPermissions(
//            this,
//            arrayOf(Manifest.permission.CAMERA),
//            CAMERA_PERMISSION_REQUEST_CODE
//        )
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Camera permission granted
//                dispatchTakePictureIntent()
//            } else {
//                // Camera permission denied
//                // Handle the denial gracefully or request permission again
//            }
//        }
//    }
//
//
//    private fun getPathFromUri(uri: Uri): String? {
//        val projection = arrayOf(MediaStore.Images.Media.DATA)
//        val cursor = contentResolver.query(uri, projection, null, null, null)
//        cursor?.use {
//            if (it.moveToFirst()) {
//                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//                return it.getString(columnIndex)
//            }
//        }
//        return null
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == RESULT_OK) {
//            when (requestCode) {
//                CAMERA_REQUEST_CODE -> {
//                    // Photo from camera captured successfully
//                    gamePhotoPaths.add(currentPhotoPath)
//                }
//                GALLERY_REQUEST_CODE -> {
//                    // Photo from gallery selected successfully
//                    val selectedImageUri: Uri? = data?.data
//                    selectedImageUri?.let {
//                        val selectedImagePath = getRealPathFromUri(it)
//                        selectedImagePath?.let { path ->
//                            gamePhotoPaths.add(path)
//                            photoAdapter.notifyDataSetChanged() // Notify the adapter that data has changed
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private fun getRealPathFromURI(uri: Uri?): String? {
//        uri?.let {
//            val projection = arrayOf(MediaStore.Images.Media.DATA)
//            val cursor = contentResolver.query(it, projection, null, null, null)
//            cursor?.use {
//                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//                if (it.moveToFirst()) {
//                    return it.getString(columnIndex)
//                }
//            }
//        }
//        return null
//    }
//
//
//    companion object {
//        private const val CAMERA_REQUEST_CODE = 1
//        private const val GALLERY_REQUEST_CODE = 2
//        private const val CAMERA_PERMISSION_REQUEST_CODE = 3
//    }
//}

package edu.put.inf151921

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class DetailsActivity : AppCompatActivity() {
    private lateinit var currentPhotoPath: String
    private lateinit var gamePhotoPaths: ArrayList<String>
    private lateinit var photoAdapter: PhotoAdapter
    private var gameId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        gamePhotoPaths = ArrayList()

        val image = intent.getStringExtra("image")
        val name = intent.getStringExtra("name")
        val description = intent.getStringExtra("description")
        val min = intent.getIntExtra("min", 0)
        val max = intent.getIntExtra("max", 0)
        val year = intent.getIntExtra("year", 0)
        val rank = intent.getIntExtra("rank", 0)
        gameId = intent.getLongExtra("gameId", 0)

        val picture: ImageView = findViewById(R.id.imageView)
        val title: TextView = findViewById(R.id.titleTextView)
        title.text = name
        val desc: TextView = findViewById(R.id.descriptionTextView)
        desc.text = "Description:\n" +
                Html.fromHtml(description)
        val players: TextView = findViewById(R.id.playersTextView)
        if (min == 0 || max == 0) {
            players.text = "No information about the number of players"
        } else {
            players.text = "Minimum number of players: $min\nMaximum number of players: $max"
        }

        val rankText: TextView = findViewById(R.id.rankTextView)
        if (rank == 0) {
            rankText.text = "No information about rank"
        } else {
            rankText.text = "Position in board games rank: $rank"
        }

        val yearText: TextView = findViewById(R.id.yearTextView)
        if (year == 0) {
            yearText.text = "No information about the year"
        } else {
            yearText.text = "Release year: $year"
        }

        Picasso.get()
            .load(image)
            .placeholder(R.drawable.placeholder_image)
            //.error(R.drawable.error_image)
            .into(picture)

        photoAdapter = PhotoAdapter(gamePhotoPaths)
    }

    private fun createGameFolder() {
        val storageDir: File? = getExternalFilesDir(null)
        val gameFolder = File(storageDir, gameId.toString())
        if (!gameFolder.exists()) {
            gameFolder.mkdirs()
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(null)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                    null
                }
                photoFile?.also {
                    val photoUri: Uri = FileProvider.getUriForFile(
                        this,
                        "edu.put.inf151921.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
                }
            }
        }
    }

    fun takePhoto(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkCameraPermission()) {
            requestCameraPermission()
        } else {
            createGameFolder()
            dispatchTakePictureIntent()
        }
    }

    private fun saveImageFile(sourceUri: Uri, gameId: Long) {
        val destinationFile = getExternalFilesDir(null)?.resolve(gameId.toString())?.resolve(UUID.randomUUID().toString() + ".jpg")

        try {
            destinationFile?.outputStream()?.use { outputStream ->
                contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    fun addPhoto(view: View) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted
                createGameFolder()
                dispatchTakePictureIntent()
            } else {
                // Camera permission denied
                // Handle the denial gracefully or request permission again
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    // Photo from camera captured successfully
                    saveImageFile(Uri.fromFile(File(currentPhotoPath)), gameId)
                }
                GALLERY_REQUEST_CODE -> {
                    // Photo from gallery selected successfully
                    val selectedImageUri: Uri? = data?.data
                    selectedImageUri?.let {
                        saveImageFile(it, gameId)
                    }
                }
            }
        }
    }


    fun seePhotos(view: View) {
        // Start a new activity or dialog to display the list of photos
        val intent = Intent(this, PhotosActivity::class.java)
        intent.putStringArrayListExtra("photoPaths", gamePhotoPaths)
        intent.putExtra("gameId",gameId)
        startActivity(intent)
    }

    companion object {
        private const val CAMERA_REQUEST_CODE = 1
        private const val GALLERY_REQUEST_CODE = 2
        private const val CAMERA_PERMISSION_REQUEST_CODE = 3
    }
}
