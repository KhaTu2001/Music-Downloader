package com.ddona.music_download_ms3_tunk.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.ddona.music_download_ms3_tunk.R
import com.ddona.music_download_ms3_tunk.databinding.ActivitySplashBinding
import com.ddona.music_download_ms3_tunk.model.playlistMusic
import com.ddona.music_download_ms3_tunk.viewmodel.SongViewModel
import kotlinx.coroutines.*


class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)



        lifecycleScope.launch {
            delay(1000)

            var intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}