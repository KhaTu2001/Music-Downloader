package com.ddona.music_download_ms3_tunk.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.SpannableStringBuilder
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ddona.music_download_ms3_tunk.R
import com.ddona.music_download_ms3_tunk.callback.SongItemClick
import com.ddona.music_download_ms3_tunk.databinding.ItemTopListenedBinding
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.ui.activity.PlayerActivity
import com.ddona.music_download_ms3_tunk.ui.activity.PlaylistDetailsActivity
import com.ddona.music_download_ms3_tunk.ui.fragment.MyMusicFragment


class MusicAdapter(
    private val context: Context,
    private var musicList: ArrayList<Data>,
//    private val playlistDetails: Boolean = false,
    private val selectionActivity: Boolean = false


) : RecyclerView.Adapter<MusicAdapter.MyHolder>() {

    class MyHolder(binding: ItemTopListenedBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.songNamed
        val article = binding.songAuthor
        val image = binding.songImg
        val index = binding.numericalOrder
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(ItemTopListenedBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.title.text = musicList[position].name
        holder.article.text = musicList[position].artistName
        holder.index.text = (position + 1).toString() + "."
        Glide.with(context)
            .load(musicList[position].image)
            .apply(
                RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).centerCrop()
            )
            .into(holder.image)

        //for play next feature

        if (selectionActivity) {
            holder.root.setOnClickListener {
                if (addSong(musicList[position])) {
                    holder.root.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorPrimary
                        )
                    )

                    Toast.makeText(context, "Song added successfully!!", Toast.LENGTH_SHORT).show()

                } else {
                    holder.root.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.background
                        )
                    )

                    Toast.makeText(context, "Song existed", Toast.LENGTH_SHORT).show()

                }
            }
        } else {
            holder.root.setOnClickListener {
                sendIntent(ref = "MusicAdapter", pos = position)
            }
        }


    }


    override fun getItemCount(): Int {
        return musicList.size
    }


    private fun sendIntent(ref: String, pos: Int) {
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("index", pos)
        intent.putExtra("from", ref)
        ContextCompat.startActivity(context, intent, null)
    }

    fun updateMusicList(searchList: ArrayList<Data>) {
        musicList = ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }

    fun addSong(data: Data): Boolean {
        MyMusicFragment.musicPlaylist.ref[PlaylistDetailsActivity.currentPlaylistPos].playlist.forEachIndexed { index, music ->
            if (data.id == music.id) {
                MyMusicFragment.musicPlaylist.ref[PlaylistDetailsActivity.currentPlaylistPos].playlist.removeAt(
                    index
                )
                return false
            }
        }
        MyMusicFragment.musicPlaylist.ref[PlaylistDetailsActivity.currentPlaylistPos].playlist.add(
            data
        )
        return true
    }

    fun refreshPlaylist() {
        musicList = ArrayList()
        musicList =
            MyMusicFragment.musicPlaylist.ref[PlaylistDetailsActivity.currentPlaylistPos].playlist
        notifyDataSetChanged()
    }
}