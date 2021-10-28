package com.example.hotc.adapters

import android.annotation.SuppressLint
import android.view.*
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.hotc.R
import com.example.hotc.util.loadImage
import java.io.File

class SlideShowAdapter : RecyclerView.Adapter<SlideShowAdapter.SlideSHowVH>() {

    var photosList: MutableList<File>? = null
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value?.toMutableList()
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = photosList?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideSHowVH {
        return SlideSHowVH(
            LayoutInflater.from(parent.context).inflate(R.layout.item_single_photo,parent,false))
    }
    override fun onBindViewHolder(holder: SlideSHowVH, position: Int) {
        photosList?.get(position)?.let { holder.bind(it) }
    }

    class SlideSHowVH(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val imageView = itemView.findViewById<ImageView>(R.id.imageSinglePhoto)

        fun bind(file: File) {

            imageView.loadImage(file)
        }
    }

}

