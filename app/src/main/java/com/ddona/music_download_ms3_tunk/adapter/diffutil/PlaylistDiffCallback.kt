package com.example.newsapp.adapter.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.model.DataX
import com.ddona.music_download_ms3_tunk.model.Song
import com.ddona.music_download_ms3_tunk.model.playlistMusic

class PlaylistDiffCallback() : DiffUtil.ItemCallback<playlistMusic>() {
    override fun areItemsTheSame(oldItem: playlistMusic, newItem: playlistMusic): Boolean {
        return  oldItem.playList_ID == newItem.playList_ID
    }
    override fun areContentsTheSame(oldItem: playlistMusic, newItem: playlistMusic): Boolean {
        return oldItem==newItem

    }
}