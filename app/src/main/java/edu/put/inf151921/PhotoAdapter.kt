package edu.put.inf151921

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.io.File

class PhotoAdapter(
    private val photoPaths: ArrayList<String>,
    private val onDeletePhoto: (Int) -> Unit
) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoPath = photoPaths[position]
        Picasso.get().load(File(photoPath)).into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return photoPaths.size
    }

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)

        init {

            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeletePhoto(position)
                }
                true
            }
        }
    }
}
