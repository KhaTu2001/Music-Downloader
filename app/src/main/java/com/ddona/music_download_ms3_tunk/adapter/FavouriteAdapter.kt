package com.ddona.music_download_ms3_tunk.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ddona.music_download_ms3_tunk.R
import com.ddona.music_download_ms3_tunk.databinding.ItemTopListenedBinding
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.ui.activity.PlayerActivity

class FavouriteAdapter(
    private val context: Context,
    private var musicList: ArrayList<Data>,

    ) : RecyclerView.Adapter<FavouriteAdapter.MyHolder>() {

    class MyHolder(binding: ItemTopListenedBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.songImg
        val name = binding.songNamed
        val root = binding.root
        var current = binding.numericalOrder
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(ItemTopListenedBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.name.text = musicList[position].name
        Glide.with(context)
            .load(musicList[position].image)
            .apply(
                RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).centerCrop()
            )
            .into(holder.image)
        holder.current.text = (position+1).toString() + "."


        holder.root.setOnClickListener {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("index", position)
            intent.putExtra("from", "FavouriteAdapter")
            intent.putExtra("FSongMusic",musicList)
            ContextCompat.startActivity(context, intent, null)
            //when play next music is clicked
        }

    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    fun updateFavourites(newList: ArrayList<Data>) {
        musicList = ArrayList()
        musicList.addAll(newList)
        notifyDataSetChanged()
    }

}