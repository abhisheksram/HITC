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
import com.example.hotc.util.loadImage
import com.example.hotc.presentation.videos.VideoPlayerActivity
import java.io.File

class VideoAdapter(val context: Context) : RecyclerView.Adapter<VideoAdapter.ImageViewHolder>() {

    var videosList: MutableList<File>? = null
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value?.toMutableList()
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = videosList?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            context,
            LayoutInflater.from(parent.context).inflate(R.layout.item_videos, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        videosList?.get(position)?.let { holder.bind(it, position) }
    }

    class ImageViewHolder(val context: Context, itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val imageView = itemView.findViewById<ImageView>(R.id.itemVideo)

        fun bind(file: File, itemPosition: Int) {

            val parent = File(file.parent!!)
            val firstChildThumbnailFolder = File("${parent.absolutePath}/Thumbnails")

            if (firstChildThumbnailFolder.exists()) {
               val thumbnails = firstChildThumbnailFolder.listFiles()

                if (thumbnails!!.isNotEmpty()) {
                    thumbnails.forEach {
                        if (it.nameWithoutExtension == file.nameWithoutExtension) {
                            val firstChildThumbnailPath = "$parent/Thumbnails/${it.name}"
                            val fileGot = File(firstChildThumbnailPath)
                            imageView.loadImage(fileGot)
                        }
                    }
                }

//                if (thumbnails!!.isNotEmpty()) {
//                        if (thumbnails[itemPosition].exists()) {
//                            imageView.loadImage(thumbnails[itemPosition])
//                        }
//                }

            }

            else imageView.loadImage(file)

            imageView.setOnClickListener {
                val intent = Intent(context, VideoPlayerActivity::class.java)
                intent.putExtra(Constants.File.videoFilePath, file)
                intent.putExtra(Constants.File.position, itemPosition)
                context.startActivity(intent)
            }
        }
    }
}