package com.example.newsapp.adapter.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.model.Song

class SongDiffCallback() : DiffUtil.ItemCallback<Data>() {
    override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
        return  oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
        return oldItem==newItem

    }
}