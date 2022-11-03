package com.ddona.music_download_ms3_tunk.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ddona.music_download_ms3_tunk.R
import com.ddona.music_download_ms3_tunk.databinding.FragmentNowPlayingBinding
import com.ddona.music_download_ms3_tunk.service.MusicService
import com.ddona.music_download_ms3_tunk.ui.activity.MainActivity
import com.ddona.music_download_ms3_tunk.ui.activity.PlayerActivity
import com.ddona.music_download_ms3_tunk.ui.activity.PlayerActivity.Companion.musicService
import kotlinx.coroutines.Runnable


class NowPlaying : Fragment() {
    companion object {
        lateinit var binding: FragmentNowPlayingBinding
    }

    lateinit var runnable: Runnable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding = FragmentNowPlayingBinding.bind(view)
        binding.root.visibility = View.INVISIBLE
        binding.PlaypauseBtnNP.setOnClickListener {

            if (PlayerActivity.isPlaying) pauseMusic() else playMusic()
        }

        binding.ClearSongBtn.setOnClickListener {
            if (musicService != null) {
                musicService!!.audioManager.abandonAudioFocus(musicService)
                musicService!!.stopForeground(true)
                musicService!!.mediaPlayer!!.stop()
                musicService = null
                binding.root.visibility = View.GONE
                MainActivity.binding.nowPlaying.visibility = View.GONE
            }
        }


        binding.constraint.setOnClickListener {
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            intent.putExtra("index", PlayerActivity.songPosition)
            intent.putExtra("from", "NowPlaying")
            ContextCompat.startActivity(requireContext(), intent, null)
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    musicService!!.mediaPlayer!!.seekTo(progress)
                    musicService!!.showNotification(if (PlayerActivity.isPlaying) R.drawable.ic_pause_song_icon else R.drawable.ic_play_song_icon)

                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })



        return binding.root
    }

    fun seekBarSetup(musicService: MusicService) {
        runnable = Runnable {
            binding.seekBar.progress = musicService!!.mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }

    override fun onResume() {
        super.onResume()


        if (musicService != null) {
            binding.root.visibility = View.VISIBLE
            binding.seekBar.progress =
                musicService!!.mediaPlayer!!.currentPosition.toFloat().toInt()
            binding.seekBar.max = musicService!!.mediaPlayer!!.duration.toFloat().toInt()

            seekBarSetup(musicService!!)

            binding.songNamed.isSelected = true
            binding.authorName.isSelected = true

            Glide.with(requireContext())
                .load(PlayerActivity.musicList[PlayerActivity.songPosition].image)
                .apply(
                    RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen)
                        .centerCrop()
                )
                .into(binding.songImg)

            binding.songNamed.text = PlayerActivity.musicList[PlayerActivity.songPosition].name
            binding.authorName.text =
                PlayerActivity.musicList[PlayerActivity.songPosition].artistName
            if (PlayerActivity.isPlaying) binding.PlaypauseBtnNP.setImageResource(R.drawable.ic_pause_song_icon)
            else binding.PlaypauseBtnNP.setImageResource(R.drawable.ic_play_song_icon)

        }

    }


    private fun playMusic() {
        PlayerActivity.isPlaying = true
        musicService!!.mediaPlayer!!.start()
        binding.PlaypauseBtnNP.setImageResource(R.drawable.ic_pause_song_icon)
        musicService!!.showNotification(R.drawable.ic_pause_song_icon)
    }

    private fun pauseMusic() {
        PlayerActivity.isPlaying = false
        musicService!!.mediaPlayer!!.pause()
        binding.PlaypauseBtnNP.setImageResource(R.drawable.ic_play_song_icon)
        musicService!!.showNotification(R.drawable.ic_play_song_icon)
    }


}