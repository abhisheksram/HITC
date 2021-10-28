package com.example.hotc.presentation.photos

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.hotc.presentation.viewModel.MyViewModel
import com.example.hotc.presentation.MainActivity
import com.example.hotc.adapters.PhotosAdapter
import com.example.hotc.common.Constants
import com.example.hotc.databinding.ActivityPhotosBinding
import java.io.File

class PhotosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotosBinding
    private val photosAdapter = PhotosAdapter(this)
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var file: File

    private lateinit var viewModelFactory: MyViewModel.ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(MyViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModelFactory = MyViewModel.ViewModelFactory(this)

        file = intent.getSerializableExtra("File") as File

        initClicks()

        setupUi()

        getPhotoLiveData(file)

        setupRecyclerView()

    }

    private fun initClicks() {

        binding.imageBack.setOnClickListener {
            onBackPressed()
        }

        binding.imageHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }

    }


    private fun setupUi() {
        sharedPreferences = this.getSharedPreferences(Constants.File.name, Context.MODE_PRIVATE)
        binding.layoutText.text = sharedPreferences.getString("name", "")?.uppercase()

        sharedPreferences = this.getSharedPreferences(Constants.File.parent, MODE_PRIVATE)
        val photosBackground =
            sharedPreferences.getString(Constants.File.photoBackground, "").toString()

        if (photosBackground.isNotEmpty()) {
            val bitmap = BitmapFactory.decodeFile(photosBackground)
            binding.imagePhotosActivity.setImageBitmap(bitmap)
        }

        editor = sharedPreferences.edit()
        editor.apply {
            putString(Constants.File.file, file.absolutePath)
        }.apply()
    }

    private fun setupRecyclerView() {

        binding.rvPhotos.layoutManager =
            StaggeredGridLayoutManager(3, RecyclerView.VERTICAL)

        binding.rvPhotos.adapter = photosAdapter
        binding.rvPhotos.setHasFixedSize(true)

    }

    private fun getPhotoLiveData(file: File) {
        viewModel.getPhotos(file)
        viewModel.photosLiveData.observe(this, {
            if (it != null) {
                photosAdapter.photosList = it.toMutableList()
            }
        })
    }

}