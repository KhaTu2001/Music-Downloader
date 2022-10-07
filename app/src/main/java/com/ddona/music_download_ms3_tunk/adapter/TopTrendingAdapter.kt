package com.ddona.music_download_ms3_tunk.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ddona.music_download_ms3_tunk.callback.TrendingSongItemClick
import com.ddona.music_download_ms3_tunk.databinding.ItemTopTrendingBinding
import com.ddona.music_download_ms3_tunk.model.Data
import com.example.newsapp.adapter.diffutil.SongDiffCallback



class TopTrendingAdapter(private val callback: TrendingSongItemClick):ListAdapter<Data,TopTrendingAdapter.ViewHolder>(
    SongDiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemTopTrendingBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    inner class ViewHolder(private var binding:ItemTopTrendingBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Data){
            if (data.audio != null) {

                Glide.with(binding.imageView)
                    .load(data.image)
                    .into(binding.imageView)
                binding.songNamed.text = data.name
                binding.songAuthor.text = data.artistName
                binding.txtRanking.text = "#"+(adapterPosition+1).toString()
                binding.root.setOnClickListener {
                    callback.TrendingOnClick(adapterPosition)
                }


            }
        }

    }

}