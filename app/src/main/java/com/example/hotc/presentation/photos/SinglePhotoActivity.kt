package com.example.hotc.presentation.photos

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.hotc.presentation.MainActivity
import com.example.hotc.adapters.SlideShowAdapter
import com.example.hotc.common.Constants
import com.example.hotc.databinding.ActivitySinglePhotoBinding
import com.example.hotc.presentation.viewModel.MyViewModel
import com.example.hotc.util.hide
import com.example.hotc.util.show
import java.io.File

class SinglePhotoActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySinglePhotoBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val slideShowAdapter = SlideShowAdapter()

    private lateinit var sliderRunnable: Runnable
    private lateinit var sliderHandler: Handler

    private lateinit var viewModelFactory: MyViewModel.ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(MyViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySinglePhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModelFactory = MyViewModel.ViewModelFactory(this)

        sliderHandler = Handler(Looper.getMainLooper())
        sliderRunnable = Runnable {
            binding.vpSinglePhoto.currentItem = binding.vpSinglePhoto.currentItem + 1
        }

        initUI()

        val position = intent.getIntExtra(Constants.File.position, 0)

        getPhotoLiveData(File(intent.getStringExtra(Constants.File.photoFilePath)!!))

        binding.vpSinglePhoto.adapter = slideShowAdapter
        binding.vpSinglePhoto.post {
            binding.vpSinglePhoto.setCurrentItem(position, false)
        }

        slideShow()

    }

    private fun getPhotoLiveData(file: File) {
        viewModel.getPhotos(file)
        viewModel.photosLiveData.observe(this, {
            if (it != null) {
                slideShowAdapter.photosList = it.toMutableList()
            }
        })
    }

    private fun slideShow(){
        binding.btnSlideShowPlay.setOnClickListener {
            sliderHandler.postDelayed(sliderRunnable, 3000)
            binding.vpSinglePhoto.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    sliderHandler.removeCallbacks(sliderRunnable)
                    sliderHandler.postDelayed(sliderRunnable, 3000)
                }
            })
            binding.btnSlideShowPlay.hide()
            binding.btnSlideShowPause.show()
            binding.relativeLayout.hide()
        }

        binding.btnSlideShowPause.setOnClickListener {
            sliderHandler.removeCallbacks(sliderRunnable)

            binding.vpSinglePhoto.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    sliderHandler.removeCallbacks(sliderRunnable)
                }
            })
            binding.btnSlideShowPause.hide()
            binding.btnSlideShowPlay.show()
            binding.relativeLayout.show()
        }
    }

    private fun initUI() {

        sharedPreferences = this.getSharedPreferences(Constants.File.parent, MODE_PRIVATE)
        val photosBackground =
            sharedPreferences.getString(Constants.File.photoBackground, "").toString()

        if (photosBackground.isNotEmpty()) {
            val bitmap = BitmapFactory.decodeFile(photosBackground)
            binding.imageSinglePhotoActivity.setImageBitmap(bitmap)
        }

        binding.imageBack.setOnClickListener {
            onBackPressed()
        }

        binding.imageHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }
    }

}