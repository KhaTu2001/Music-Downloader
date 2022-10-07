package com.example.newsapp.adapter.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.model.DataX
import com.ddona.music_download_ms3_tunk.model.Song

class GenreDiffCallback() : DiffUtil.ItemCallback<DataX>() {
    override fun areItemsTheSame(oldItem: DataX, newItem: DataX): Boolean {
        return  oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: DataX, newItem: DataX): Boolean {
        return oldItem==newItem

    }
}