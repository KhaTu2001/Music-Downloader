package com.ddona.music_download_ms3_tunk.model

import android.util.Log
import com.ddona.music_download_ms3_tunk.R
import com.ddona.music_download_ms3_tunk.ui.activity.PlayerActivity
import com.ddona.music_download_ms3_tunk.ui.activity.PlayerActivity.Companion.musicService
import com.ddona.music_download_ms3_tunk.ui.activity.PlayerActivity.Companion.repeatOneSong
import com.ddona.music_download_ms3_tunk.ui.fragment.DownloandingFragment
import com.ddona.music_download_ms3_tunk.ui.fragment.FavouriteFragment
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt
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

fun comparePaths(path: String): Boolean {
    for (i in DownloandingFragment.musicList)
        if (path == "/storage/emulated/0/Music Downloader/"+i.name+".mp3") return true
    Log.d("asdsad", "comparePaths: $path")
    return false
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


fun exitApplication() {
    if (musicService != null) {
        musicService!!.audioManager.abandonAudioFocus(musicService)
        musicService!!.stopForeground(true)
        musicService!!.mediaPlayer!!.release()
        musicService = null
    }
    exitProcess(1)
}

fun formatFileSize(size: Double):String {
    if(size.isNaN()){
     return "0"
    }
    else{
        val newSize = size.roundToInt()
        return newSize.toString()
    }


}

