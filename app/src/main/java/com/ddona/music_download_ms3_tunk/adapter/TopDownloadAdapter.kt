package com.ddona.music_download_ms3_tunk.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ddona.music_download_ms3_tunk.callback.DownloadSongItemClick
import com.ddona.music_download_ms3_tunk.databinding.ItemTopDownloadBinding
import com.ddona.music_download_ms3_tunk.model.Data
import com.example.newsapp.adapter.diffutil.SongDiffCallback



class TopDownloadAdapter(private val callback: DownloadSongItemClick):ListAdapter<Data,TopDownloadAdapter.ViewHolder>(
    SongDiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(ItemTopDownloadBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    inner class ViewHolder(private var binding:ItemTopDownloadBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Data) {

            if (data.audio != null) {

                Glide.with(binding.songImg)
                    .load(data.image)
                    .into(binding.songImg)
                binding.songNamed.text = data.name
                binding.songAuthor.text = data.artistName

                binding.itemsSong.setOnClickListener {
                    callback.downloadOnClick(adapterPosition)
                }

            }
        }
    }
}