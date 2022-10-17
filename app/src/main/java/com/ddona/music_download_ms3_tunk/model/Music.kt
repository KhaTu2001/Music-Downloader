package com.ddona.music_download_ms3_tunk.model

import com.ddona.music_download_ms3_tunk.R
import com.ddona.music_download_ms3_tunk.ui.activity.PlayerActivity
import com.ddona.music_download_ms3_tunk.ui.activity.PlayerActivity.Companion.musicService
import com.ddona.music_download_ms3_tunk.ui.activity.PlayerActivity.Companion.repeatOneSong
import com.ddona.music_download_ms3_tunk.ui.fragment.NowPlaying
import com.example.newsapp.fragments.FavouriteFragment
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess


fun formatDuration(duration: Long): String {
    val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
    val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) -
            minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
    return String.format("%02d:%02d", minutes, seconds)
}

fun setSongPosition(increment: Boolean, isClickNextPrev: Boolean) {
    if ((PlayerActivity.repeat && !PlayerActivity.repeatOneSong) ){

        if (increment) {
            if (PlayerActivity.musicList.size - 1 == PlayerActivity.songPosition){
                PlayerActivity.songPosition = 0
            }
            else ++PlayerActivity.songPosition
        } else {
            if (0 == PlayerActivity.songPosition)
                PlayerActivity.songPosition = PlayerActivity.musicList.size - 1
            else --PlayerActivity.songPosition
        }
        PlayerActivity.isClickNextPrev = false
    }

    if(!PlayerActivity.repeat && !PlayerActivity.repeatOneSong ){

        if (increment) {
            if (PlayerActivity.musicList.size - 1 == PlayerActivity.songPosition){
            }
            else ++PlayerActivity.songPosition
        }
        if(!increment) {
            if (0 == PlayerActivity.songPosition)
                PlayerActivity.songPosition = 0
            else --PlayerActivity.songPosition
        }
        PlayerActivity.isClickNextPrev = false
    }

    if ((!PlayerActivity.repeat && PlayerActivity.repeatOneSong )|| (isClickNextPrev && PlayerActivity.repeatOneSong)) {

        if (increment &&  isClickNextPrev) {
            if (PlayerActivity.musicList.size - 1 == PlayerActivity.songPosition)
                PlayerActivity.songPosition = PlayerActivity.musicList.size - 1
            else ++PlayerActivity.songPosition
        }
        if(!increment) {
            if (0 == PlayerActivity.songPosition)
                PlayerActivity.songPosition = 0
            else --PlayerActivity.songPosition
        }
        PlayerActivity.isClickNextPrev = false
    }
}

fun shufferPosition(isShuffer:Boolean){
    if(isShuffer && !repeatOneSong){
        PlayerActivity.musicList.shuffle()
    }
    else{
        PlayerActivity.musicList = PlayerActivity.oldmusicList
        PlayerActivity.songPosition = PlayerActivity.oldmusicList.indexOf(PlayerActivity.musicList[PlayerActivity.songPosition])
    
    }

}

fun pauseMusic() {
    PlayerActivity.isPlaying = false
    musicService!!.mediaPlayer!!.pause()
    NowPlaying.binding.PlaypauseBtnNP.setImageResource(R.drawable.ic_play_song_icon)
    musicService!!.showNotification(R.drawable.ic_play_song_icon)
}

fun favouriteChecker(id: String): Int {
    PlayerActivity.isFavourite = false
    FavouriteFragment.favouriteList.forEachIndexed { index, data ->
        if (id == data.id) {
            PlayerActivity.isFavourite = true
            return index
        }
    }
    return -1
}

fun favouriteCheckerID(id: String): Int {
    FavouriteFragment.favouriteList.forEachIndexed { index, data ->
        if (id == data.id) {
            return index
        }
    }
    return -1
}

fun checkPlaylist(playlist: ArrayList<Data>): ArrayList<Data>{
    playlist.forEachIndexed { index, music ->
        val file = File(music.audio)
        if(!file.exists())
            playlist.removeAt(index)
    }
    return playlist
}

fun exitApplication() {
    if (musicService != null) {
        musicService!!.audioManager.abandonAudioFocus(musicService)
        musicService!!.stopForeground(true)
        musicService!!.mediaPlayer!!.release()
        musicService = null
    }
    exitProcess(1)
}

