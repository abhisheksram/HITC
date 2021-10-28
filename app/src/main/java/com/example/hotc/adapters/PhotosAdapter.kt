package com.example.hotc.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.hotc.R
import com.example.hotc.common.Constants
import com.example.hotc.presentation.photos.SinglePhotoActivity
import com.example.hotc.util.loadImageWithPlaceHolder
import java.io.File

class PhotosAdapter(val context: Context) : RecyclerView.Adapter<PhotosAdapter.ImageViewHolder>() {

    var photosList: MutableList<File>? = null
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value?.toMutableList()
            notifyDataSetChanged()
        }


    override fun getItemCount(): Int = photosList?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            context,
            LayoutInflater.from(parent.context).inflate(R.layout.item_photos, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        photosList?.get(position)?.let { holder.bind(it, position) }
    }


    class ImageViewHolder(val context: Context, itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val imageView = itemView.findViewById<ImageView>(R.id.imagePhotoList)

        fun bind(file: File, itemPosition: Int) {
            imageView.loadImageWithPlaceHolder(file)
            imageView.setOnClickListener {
                val intent = Intent(context, SinglePhotoActivity::class.java)
                intent.putExtra(Constants.File.photoFilePath, file.parent)
                intent.putExtra(Constants.File.position, itemPosition)
                context.startActivity(intent)
            }
        }
    }
}

