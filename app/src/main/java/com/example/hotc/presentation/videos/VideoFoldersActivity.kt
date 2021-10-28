package com.example.hotc.presentation.videos

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hotc.presentation.MainActivity
import com.example.hotc.adapters.VideoFolderAdapter
import com.example.hotc.common.Constants
import com.example.hotc.databinding.ActivityVideoFolderBinding
import com.example.hotc.util.showToast
import com.example.hotc.util.sortItemsByFolder
import java.io.File
import java.util.ArrayList

class VideoFoldersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoFolderBinding

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private val videoFolderAdapter= VideoFolderAdapter(this)
    private lateinit var videoFolderList : List<File>
    private  var videoFolderArray = ArrayList<File>()

    private lateinit var backgroundFolder: String
    private lateinit var videoBackground : String
    private lateinit var videosFolder : String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initClicks()
        sharedPreferences = this.getSharedPreferences(Constants.File.parent, MODE_PRIVATE)
        editor = sharedPreferences.edit()
        videosFolder = sharedPreferences.getString(Constants.File.videosFolder,"").toString()

        videoBackground = "$videosFolder/Background"

        val videosBackgroundImage=  File(videoBackground)

        backgroundFolder = sharedPreferences.getString(Constants.File.backgroundFolder,"").toString()
        val backgroundImageHome =  File(backgroundFolder)


        if (videosBackgroundImage.exists()) {
            val videoFiles = videosBackgroundImage.listFiles()

            if (videoFiles!!.isNotEmpty()) {
                val bitmap = BitmapFactory.decodeFile(videoFiles[0].absolutePath)
                binding.imageVideosFolderActivity.setImageBitmap(bitmap)
                editor.apply {
                    putString(Constants.File.videoBackground, videoFiles[0].absolutePath)
                }.apply()
            }
        }

       else if (backgroundImageHome.exists()) {
            val backgroundPhotoList = backgroundImageHome.listFiles()

            if (backgroundPhotoList!!.isNotEmpty()) {
                val bitmap = BitmapFactory.decodeFile(backgroundPhotoList[0].absolutePath)
                binding.imageVideosFolderActivity.setImageBitmap(bitmap)
                editor.apply {
                    putString(Constants.File.videoBackground, backgroundPhotoList[0].absolutePath)
                }.apply()
            }
        }

        val folderList = getFolders().toList()

        if (folderList.isNotEmpty()){
            videoFolderAdapter.setItems(sortItemsByFolder(folderList))
        }
        else showToast("No Videos are Available")

        binding.rvVideosFolder.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL,false)
        binding.rvVideosFolder.adapter = videoFolderAdapter

    }

    private fun getFolders(): ArrayList<File> {

        val videoFile = File(videosFolder)
        val videoList = videoFile.listFiles()

        videoList!!.forEach got@{ folder ->
            if (folder.isDirectory) {
                val videosFoldersList = folder.listFiles()
                if (videosFoldersList!!.isNotEmpty()) {
                    videosFoldersList.forEach { file ->
                        if (file.absolutePath.contains(".mp4") || file.absolutePath.contains(".mpeg")
                            || file.absolutePath.contains(".mkv") || file.absolutePath.contains(".webm")
                            || file.absolutePath.contains(".avi")) {
                            videoFolderList = (folder.listFiles()!!).toList()
                            videoFolderArray.add(videoFolderList[0])
                            return@got
                        }
                    }
                }
            }
        }

        return videoFolderArray
    }

    private fun initClicks(){
        binding.imageBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.imageHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }
    }


}