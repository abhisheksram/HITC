package com.example.hotc.presentation.videos

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hotc.presentation.viewModel.MyViewModel
import com.example.hotc.presentation.MainActivity
import com.example.hotc.adapters.VideoAdapter
import com.example.hotc.common.Constants
import com.example.hotc.databinding.ActivityVideosBinding
import java.io.File

class VideosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideosBinding
    private val videoAdapter = VideoAdapter(this)
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var viewModelFactory: MyViewModel.ViewModelFactory
    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(MyViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModelFactory = MyViewModel.ViewModelFactory(this)

        initUI()

        getVideosLiveData(intent.getSerializableExtra("File") as File)

        setupRecyclerView()

    }

    private fun initUI() {

        sharedPreferences = this.getSharedPreferences(Constants.File.name, Context.MODE_PRIVATE)
        binding.layoutText.text = sharedPreferences.getString("name", null)?.uppercase()

        sharedPreferences = this.getSharedPreferences(Constants.File.parent, MODE_PRIVATE)
        val videosBackground =
            sharedPreferences.getString(Constants.File.videoBackground, "").toString()

        if (videosBackground.isNotEmpty()) {
            val bitmap = BitmapFactory.decodeFile(videosBackground)
            binding.imageVideosActivity.setImageBitmap(bitmap)
        }

        binding.imageBack.setOnClickListener {
            onBackPressed()
        }

        binding.imageHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }
    }

    private fun getVideosLiveData(file: File) {
        viewModel.getVideos(file)
        viewModel.videosLiveData.observe(this, {
            if (it != null) {
                videoAdapter.videosList = it.toMutableList()
            }
        })
    }

    private fun setupRecyclerView() {
        binding.rvVideos.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)

        binding.rvVideos.adapter = videoAdapter
        binding.rvVideos.setHasFixedSize(true)

    }

}