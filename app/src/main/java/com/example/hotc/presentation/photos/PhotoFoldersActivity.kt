package com.example.hotc.presentation.photos

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hotc.presentation.MainActivity
import com.example.hotc.adapters.PhotoFolderAdapter
import com.example.hotc.common.Constants
import com.example.hotc.databinding.ActivityPhotoFolderBinding
import com.example.hotc.util.showToast
import com.example.hotc.util.sortItemsByFolder
import kotlinx.coroutines.*
import java.io.File
import java.util.ArrayList

class PhotoFoldersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotoFolderBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private val photoFolderAdapter = PhotoFolderAdapter(this)
    private lateinit var photoFolderList: List<File>
    private var photoFolderArray = ArrayList<File>()

    private lateinit var backgroundFolder: String
    private lateinit var photosBackground: String
    private lateinit var photosFolder: String


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initClicks()
        sharedPreferences = this.getSharedPreferences(Constants.File.parent, MODE_PRIVATE)
        editor = sharedPreferences.edit()

        photosFolder = sharedPreferences.getString(Constants.File.photosFolder, "").toString()

        photosBackground = "$photosFolder/Background"
        val photosBackgroundImage = File(photosBackground)

        backgroundFolder =
            sharedPreferences.getString(Constants.File.backgroundFolder, "").toString()
        val backgroundImageHome = File(backgroundFolder)

        if (photosBackgroundImage.exists()) {
            val imageList = photosBackgroundImage.listFiles()

            if (imageList!!.isNotEmpty()) {
                val bitmap = BitmapFactory.decodeFile(imageList[0].absolutePath)
                binding.imagePhotoFolderActivity.setImageBitmap(bitmap)
                editor.apply {
                    putString(Constants.File.photoBackground, imageList[0].absolutePath)
                }.apply()
            }
        } else if (backgroundImageHome.exists()) {
            val backgroundPhotoList = backgroundImageHome.listFiles()

            if (backgroundPhotoList!!.isNotEmpty()) {
                val bitmap = BitmapFactory.decodeFile(backgroundPhotoList[0].absolutePath)
                binding.imagePhotoFolderActivity.setImageBitmap(bitmap)
                editor.apply {
                    putString(Constants.File.photoBackground, backgroundPhotoList[0].absolutePath)
                }.apply()
            }
        }

        val folderList = getFolders().toList()

        if (folderList.isNotEmpty()) {
            photoFolderAdapter.setItems(sortItemsByFolder(folderList))
        }
        else showToast("No Photos are Available")

        binding.rvPhotoFolder.layoutManager = LinearLayoutManager(
            applicationContext,
            LinearLayoutManager.HORIZONTAL, false
        )
        binding.rvPhotoFolder.adapter = photoFolderAdapter


    }

    private fun getFolders(): ArrayList<File> {

        val photoFile = File(photosFolder)
        val photosList = photoFile.listFiles()

        photosList!!.forEach got@{ folder ->
            if (folder.isDirectory && folder.name != "Background") {
                val photosFoldersList = folder.listFiles()
                if (photosFoldersList!!.isNotEmpty()) {

                    photosFoldersList.forEach { file ->
                        if (file.absolutePath.contains(".jpg") || file.absolutePath.contains(".png")
                            || file.absolutePath.contains(".jpeg") || file.absolutePath.contains(".raw")) {
                            photoFolderList = (folder.listFiles()!!).toList()
                            photoFolderArray.add(photoFolderList[0])
                            return@got
                        }
                    }
                }
            }
        }

        return photoFolderArray
    }


    private fun initClicks() {
        binding.imageBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.imageHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }
    }

}