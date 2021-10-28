package com.example.hotc.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hotc.R
import com.example.hotc.common.Constants
import com.example.hotc.presentation.photos.PhotosActivity
import com.example.hotc.util.loadImage
import java.io.File


class PhotoFolderAdapter(val context: Context) : RecyclerView.Adapter<PhotoFolderAdapter.VH>() {

    private var items = mapOf<File, List<File>>()
    private var indexes = listOf<File>()

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(fileMap: Map<File, List<File>>) {
        items = fileMap
        indexes = fileMap.keys.toList()
        notifyDataSetChanged()
    }
    inner class VH(val context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {


        private val imageView = itemView.findViewById<ImageView>(R.id.imageFolders)
        private val titleTextView = itemView.findViewById<TextView>(R.id.tvFolders)

        private lateinit var sharedPreferences: SharedPreferences
        private lateinit var editor: SharedPreferences.Editor


        fun bind(files: List<File>?) {
            (files?.isNotEmpty()).let {
                val firstChild = files!![0]
                val parent = File(firstChild.parent!!)
                val name = parent.nameWithoutExtension.uppercase()
                titleTextView.text = name
                imageView.loadImage(firstChild)
                itemView.setOnClickListener {
                    sharedPreferences =
                        context.getSharedPreferences(Constants.File.name, Context.MODE_PRIVATE)
                    editor = sharedPreferences.edit()
                    editor.apply {
                        putString("name", parent.name)
                    }.apply()
                    val intent = Intent(context, PhotosActivity::class.java)
                    intent.putExtra("File",parent)
                    context.startActivity(intent)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            context,
            LayoutInflater.from(parent.context).inflate(R.layout.item_folders, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[indexes[position]])
    }

    override fun getItemCount(): Int = items.size

}