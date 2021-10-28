package com.example.hotc.presentation

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.os.Build.VERSION.SDK_INT
import android.provider.Settings
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.hotc.R
import com.example.hotc.util.showToast


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (checkStoragePermission()) {

            playSplashVideo()

        } else {

            requestStoragePermission()

        }
    }

    private fun playSplashVideo() {
        val videoSplash = findViewById<VideoView>(R.id.videoSplash)

        videoSplash.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.hotc_introduction))
        videoSplash.start()
        videoSplash.setOnCompletionListener {
            nextActivity()
        }

    }

    private fun nextActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun checkStoragePermission(): Boolean {

        return if (SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val readCheck = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)
            val writeCheck = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
            readCheck == PackageManager.PERMISSION_GRANTED && writeCheck == PackageManager.PERMISSION_GRANTED
        }
    }


    private fun requestStoragePermission() {


        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))
                activityResultLauncher.launch(intent)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                activityResultLauncher.launch(intent)
            }
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    READ_EXTERNAL_STORAGE,
                    WRITE_EXTERNAL_STORAGE
                ), 101
            )
        }

    }

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    showToast("Permission Granted")
                    playSplashVideo()
                    nextActivity()
                } else {
                    showToast("Permission Denied")
                }
            }
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Storage Permission Granted", Toast.LENGTH_LONG)
                    .show()
                playSplashVideo()

            } else {
                val requestStorageAgain =
                    shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)
                if (requestStorageAgain) Toast.makeText(
                    applicationContext, "Storage Permission Denied",
                    Toast.LENGTH_LONG
                ).show()
                else {
                    Toast.makeText(
                        this,
                        "Storage Permission Denied, Go to settings and enable Permission",
                        Toast.LENGTH_LONG
                    ).show()

                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Permission Required")
                    builder.setMessage(
                        "Storage permission is required to access photos ad videos. " +
                                "\nClick Permit to go to settings and enable the permission"
                    )
                    builder.setCancelable(false)
                    builder.setPositiveButton("Permit")
                    { _, _ ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri: Uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    builder.setNegativeButton("Cancel")
                    { dialog, _ ->

                        dialog.dismiss()
                    }
                    val alert = builder.create()
                    alert.show()
                }
            }
        }
    }

//    override fun onResume() {
//        super.onResume()
//        nextActivity()
//    }

}