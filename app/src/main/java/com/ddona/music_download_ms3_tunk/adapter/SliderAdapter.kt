package com.ddona.music_download_ms3_tunk.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.ddona.music_download_ms3_tunk.callback.SliderSongItemClick
import com.ddona.music_download_ms3_tunk.databinding.ItemsSliderShowBinding
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.model.setSongPosition

import com.example.newsapp.adapter.diffutil.SongDiffCallback


class SliderAdapter(
    private val listSong: ArrayList<Data>,
    private val viewPager2: ViewPager2,
    private val callback: SliderSongItemClick
) :
    RecyclerView.Adapter<SliderAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemsSliderShowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listSong[position])
        if (position == listSong.size - 1) {
            viewPager2.post(runnable)
        }

    }

    inner class ViewHolder(private val binding: ItemsSliderShowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Data) {
            if (data.audio != null) {

                Glide.with(binding.imageView)
                    .load(data.image)
                    .into(binding.imageView)
                binding.songNamed.text = data.name
                binding.songAuthor.text = data.artistName
                binding.playPauseAction.setOnClickListener {
                    callback.SliderOnClick(adapterPosition)
                }
            }
        }
    }

    private val runnable = Runnable {
        listSong.addAll(listSong)
        notifyDataSetChanged()
    }

    override fun getItemCount() = listSong.size


}