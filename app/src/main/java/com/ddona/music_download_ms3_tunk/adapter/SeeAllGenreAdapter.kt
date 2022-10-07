package com.ddona.music_download_ms3_tunk.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ddona.music_download_ms3_tunk.callback.GenreItemClick
import com.ddona.music_download_ms3_tunk.databinding.ItemGenreBinding
import com.ddona.music_download_ms3_tunk.databinding.ItemSeeAllGenresBinding
import com.ddona.music_download_ms3_tunk.model.DataX
import com.example.newsapp.adapter.diffutil.GenreDiffCallback


class SeeAllGenreAdapter(private val callback: GenreItemClick):ListAdapter<DataX,SeeAllGenreAdapter.ViewHolder>(
    GenreDiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemSeeAllGenresBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    inner class ViewHolder(private var binding:ItemSeeAllGenresBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(dataX: DataX){

            Glide.with(binding.genreImage)
                .load("http://marstechstudio.com/img-msd/"+dataX.image)
                .into(binding.genreImage)
            binding.genreNamed.text = dataX.name
            binding.itemsGenre.setOnClickListener{
                callback.GenreOnClick(dataX.keySearch,dataX.name)
            }



        }

    }
}