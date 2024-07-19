package phocidae.mirounga.leonina.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import phocidae.mirounga.leonina.R

class PhotoAdapter(private val photoPaths: MutableList<String>) :
    RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoImageView: ImageView = itemView.findViewById(R.id.photoImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoPath = photoPaths[position]
        // TODO Glide u otra biblioteca de carga de imágenes puede ser útil aquí
        // Glide.with(holder.itemView.context).load(photoPath).into(holder.photoImageView)
        holder.photoImageView.setImageURI(Uri.parse(photoPath))
    }

    override fun getItemCount(): Int {
        return photoPaths.size
    }
}
