package com.harshRajpurohit.musicPlayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ddona.music_download_ms3_tunk.App
import com.ddona.music_download_ms3_tunk.R
import com.ddona.music_download_ms3_tunk.model.exitApplication
import com.ddona.music_download_ms3_tunk.model.setSongPosition
import com.ddona.music_download_ms3_tunk.ui.activity.MainActivity
import com.ddona.music_download_ms3_tunk.ui.activity.PlayerActivity
import com.ddona.music_download_ms3_tunk.ui.fragment.NowPlaying


class NotificationReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            App.PREVIOUS -> prevNextSong(increment = false, context = context!!)
            App.PLAY -> if(PlayerActivity.isPlaying) pauseMusic() else playMusic()
            App.NEXT -> prevNextSong(increment = true, context = context!!)
            App.EXIT ->{
                if (PlayerActivity.musicService != null) exitApplication()
            }
        }
    }

    private fun playMusic(){
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.ic_pause_song_icon)
        PlayerActivity.binding.playPauseBtn.setImageResource(R.drawable.ic_pause_song)
        NowPlaying.binding.PlaypauseBtnNP.setImageResource(R.drawable.ic_pause_song_icon)

    }

    private fun pauseMusic(){
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotification(R.drawable.ic_play_song_icon)
        PlayerActivity.binding.playPauseBtn.setImageResource(R.drawable.ic_play_pause_song)
        NowPlaying.binding.PlaypauseBtnNP.setImageResource(R.drawable.ic_play_song_icon)

    }

    private fun prevNextSong(increment: Boolean, context: Context){
        Log.d("abdcd","receiver")
        setSongPosition(increment = increment, isClickNextPrev = true)
        PlayerActivity.musicService!!.createMediaPlayer()

        Glide.with(context)
            .load(PlayerActivity.musicList[PlayerActivity.songPosition].image)
            .apply(RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).centerCrop())
            .into(PlayerActivity.binding.songImg)
        PlayerActivity.binding.songNamed.text = PlayerActivity.musicList[PlayerActivity.songPosition].name

        Glide.with(context)
            .load(PlayerActivity.musicList[PlayerActivity.songPosition].image)
            .apply(RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).centerCrop())
            .into(NowPlaying.binding.songImg)
        NowPlaying.binding.songNamed.text = PlayerActivity.musicList[PlayerActivity.songPosition].name
//        PlayerActivity.fIndex = favouriteChecker(PlayerActivity.musicList[PlayerActivity.songPosition].id)
//        if(PlayerActivity.isFavourite) PlayerActivity.binding.favouriteBtnPA.setImageResource(R.drawable.favourite_icon)
//        else PlayerActivity.binding.favouriteBtnPA.setImageResource(R.drawable.favourite_empty_icon)
        playMusic()

    }
}