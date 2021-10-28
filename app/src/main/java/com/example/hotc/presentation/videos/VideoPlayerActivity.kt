package com.example.hotc.presentation.videos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hotc.presentation.MainActivity
import com.example.hotc.common.Constants
import com.example.hotc.databinding.ActivityVideoPlayerBinding
import com.example.hotc.util.hide
import com.example.hotc.util.showToast
import com.google.android.exoplayer2.*
import java.io.File
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.media.AudioManager
import android.view.KeyEvent
import com.example.hotc.util.show
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import com.google.android.exoplayer2.PlaybackParameters
import android.widget.ImageView


class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoPlayerBinding
    private lateinit var exoPlayer: Player

    private lateinit var muteAudio: ImageView
    private lateinit var unMuteAudio: ImageView
    private lateinit var volumeSeek: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        volumeSeek = findViewById(com.example.hotc.R.id.volumeControl)
        muteAudio = findViewById(com.example.hotc.R.id.muteAudio)
        unMuteAudio = findViewById(com.example.hotc.R.id.unMuteAudio)

        initClicks()
        setOrientation()
        volumeController()
        seekToPosition()

        val videoPath =
            (intent.getSerializableExtra(Constants.File.videoFilePath) as File).absolutePath.toString()
        startVideoPlayer(videoPath)
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

    private var playerListener = object : Player.Listener {
        override fun onRenderedFirstFrame() {
            super.onRenderedFirstFrame()
            binding.playerView.useController = true

        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            showToast("${error.message}")
        }
    }

    private fun startVideoPlayer(path: String) {

        val defaultRenderersFactory =
            DefaultRenderersFactory(this).setEnableAudioTrackPlaybackParams(true)
        exoPlayer = ExoPlayer.Builder(this, defaultRenderersFactory).build()

        exoPlayer.setMediaItem(MediaItem.fromUri(path))
        exoPlayer.prepare()
        exoPlayer.addListener(playerListener)
        exoPlayer.seekTo(0)
        exoPlayer.playbackParameters = PlaybackParameters(1f)
        exoPlayer.play()

        binding.playerView.player = exoPlayer

    }

    private fun setOrientation() {
        val btnFullScreenEntry : ImageView = findViewById(com.example.hotc.R.id.btnFullScreenEntry)
        val btnFullScreenExit : ImageView = findViewById(com.example.hotc.R.id.btnFullScreenExit)
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            btnFullScreenEntry.hide()
            btnFullScreenExit.show()
            binding.relativeLayout.hide()
        }
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            btnFullScreenEntry.show()
            btnFullScreenExit.hide()
            binding.relativeLayout.show()
        }

        btnFullScreenExit.setOnClickListener {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
            btnFullScreenExit.hide()
            btnFullScreenEntry.show()
            binding.relativeLayout.show()
        }

        btnFullScreenEntry.setOnClickListener {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
            btnFullScreenEntry.hide()
            btnFullScreenExit.show()
            binding.relativeLayout.hide()
        }
    }

    private fun seekToPosition() {

        val exoRewind : ImageView =  findViewById(R.id.exo_rew)
        val exoForward : ImageView = findViewById(R.id.exo_ffwd)

        exoRewind.setOnClickListener {
            exoPlayer.seekTo(exoPlayer.currentPosition - 10000)
        }

        exoForward.setOnClickListener {
            exoPlayer.seekTo(exoPlayer.currentPosition + 10000)
        }
    }

    private fun volumeController() {
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        volumeSeek.max = maxVolume
        volumeSeek.progress = currentVolume

        if (currentVolume == 0) {
            muteAudio.hide()
            unMuteAudio.show()
        } else {
            muteAudio.show()
            unMuteAudio.hide()
        }

        volumeSeek.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
                if (progress == 0) {
                    muteAudio.hide()
                    unMuteAudio.show()
                } else {
                    muteAudio.show()
                    unMuteAudio.hide()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        muteAudio.setOnClickListener {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0)
            muteAudio.hide()
            unMuteAudio.show()
        }

        unMuteAudio.setOnClickListener {
            if (volumeSeek.progress == 0){
                unMuteAudio.show()
                muteAudio.hide()
            }
            else {
                audioManager.adjustStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_UNMUTE,
                    0
                )
                unMuteAudio.hide()
                muteAudio.show()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                volumeSeek.progress =
                    if (volumeSeek.progress + 1 > volumeSeek.max) volumeSeek.max else volumeSeek.progress + 1
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                volumeSeek.progress =
                    if (volumeSeek.progress - 1 < 0) 0 else volumeSeek.progress - 1
            }
            KeyEvent.KEYCODE_VOLUME_MUTE -> {
                muteAudio.hide()
                unMuteAudio.show()
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onResume() {
        super.onResume()

        exoPlayer.playWhenReady = true
        exoPlayer.play()

    }

    override fun onPause() {
        super.onPause()

        exoPlayer.pause()
        exoPlayer.playWhenReady = false

    }

    override fun onStop() {
        super.onStop()

        exoPlayer.pause()
        exoPlayer.playWhenReady = false

    }

    override fun onDestroy() {
        super.onDestroy()

        exoPlayer.removeListener(playerListener)
        exoPlayer.stop()
        exoPlayer.clearMediaItems()

    }
}