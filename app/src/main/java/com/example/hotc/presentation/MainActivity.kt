package com.example.hotc.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import com.example.hotc.common.Constants
import com.example.hotc.databinding.ActivityMainBinding
import com.example.hotc.presentation.photos.PhotoFoldersActivity
import com.example.hotc.presentation.videos.VideoFoldersActivity
import com.example.hotc.util.gone
import com.example.hotc.util.show
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val folderPath: String = "/emulated/0/HOTC/"
    private var fileName = ""
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var photosFolderPath : String
    private lateinit var videosFolderPath : String
    private lateinit var parentFolderPath : String
    private lateinit var backgroundFolderPath : String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            binding.tvEvent.text = "Storage Not Available"
            binding.btnPhotos.gone()
            binding.btnVideos.gone()
        } else {

            val mainFolderPath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Environment.getStorageDirectory().toString() + folderPath
            } else {
                "/storage/$folderPath"
            }

            val folder = File(mainFolderPath)

            if (folder.exists()) {

                val files = folder.listFiles()

                if (files!!.isEmpty()) {
                    binding.tvEvent.text = "No Events are available"

                } else {
                    fileName = if (files[0].isDirectory) {
                        files[0].name
                    } else {
                        "No Events are available"
                    }

                    binding.tvEvent.text = fileName.uppercase()

                }
            } else binding.tvEvent.text = "No Files are available"

            backgroundFolderPath = "$mainFolderPath$fileName/Background"
            val backgroundImageHome = File(backgroundFolderPath)
            if (backgroundImageHome.exists()) {
                val imageList = backgroundImageHome.listFiles()

                if (imageList!!.isNotEmpty()) {
                    val bitmap = BitmapFactory.decodeFile(imageList[0].absolutePath)
                    binding.imageMainActivity.setImageBitmap(bitmap)
                }
            }

            val photosFolder = File("$mainFolderPath$fileName/Photos")
            val videosFolder = File("$mainFolderPath$fileName/Videos")
            parentFolderPath = "$mainFolderPath$fileName"
            photosFolderPath = "$mainFolderPath$fileName/Photos"
            videosFolderPath = "$mainFolderPath$fileName/Videos"

            if (photosFolder.exists()) {
                binding.btnPhotos.show()
            } else binding.btnPhotos.gone()

            if (videosFolder.exists()) {
                binding.btnVideos.show()
            } else binding.btnVideos.gone()

            sharedPreferences = this.getSharedPreferences(Constants.File.parent, MODE_PRIVATE)
            editor = sharedPreferences.edit()

            editor.apply {
                putString(Constants.File.parentFolder, parentFolderPath)
                putString(Constants.File.photosFolder, photosFolderPath)
                putString(Constants.File.videosFolder, videosFolderPath)
                putString(Constants.File.backgroundFolder, backgroundFolderPath)
            }.apply()

            binding.btnPhotos.setOnClickListener {
                startActivity(Intent(this, PhotoFoldersActivity::class.java))
            }

            binding.btnVideos.setOnClickListener {
                startActivity(Intent(this, VideoFoldersActivity::class.java))
            }

        }
    }

}