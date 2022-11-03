package com.ddona.music_download_ms3_tunk.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.ddona.music_download_ms3_tunk.databinding.ActivitySplashBinding
import kotlinx.coroutines.*
import java.io.File


class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val PERMISSION_REQUEST_CODE = 7

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

            createDirectory("Music Downloader")
            Log.d("asdsad", "onCreate: ")

        }else
        {

            askPermission()
        }

        lifecycleScope.launch {
            delay(1000)

            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String?>,
        @NonNull grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createDirectory("Music Downloader")
            } else {
                Toast.makeText(this@SplashActivity, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun askPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }


    private fun createDirectory(folderName: String) {
        val file = File(Environment.getExternalStorageDirectory(), folderName)
        if (!file.exists()) {
            file.mkdir()
            Toast.makeText(this@SplashActivity, "Successful", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this@SplashActivity, "Folder Already Exists", Toast.LENGTH_SHORT).show()
        }
    }
}