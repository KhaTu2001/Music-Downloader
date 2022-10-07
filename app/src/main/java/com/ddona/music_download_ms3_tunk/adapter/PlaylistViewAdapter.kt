package com.ddona.music_download_ms3_tunk.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ddona.music_download_ms3_tunk.R
import com.ddona.music_download_ms3_tunk.databinding.ItemPlaylistBinding
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.model.Playlist
import com.ddona.music_download_ms3_tunk.ui.activity.MainActivity
import com.ddona.music_download_ms3_tunk.ui.activity.PlaylistDetailsActivity
import com.ddona.music_download_ms3_tunk.ui.fragment.MyMusicFragment

private lateinit var adapter: MusicAdapter

class PlaylistViewAdapter(
    private val context: Context,
    private var playlistList: ArrayList<Playlist>,
    private val addToPlaylist: Boolean = false,
    private val music: Data?
) : RecyclerView.Adapter<PlaylistViewAdapter.MyHolder>() {

    class MyHolder(binding: ItemPlaylistBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.playlistImg
        val name = binding.playlistName
        val count = binding.newCountSong
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(ItemPlaylistBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {

        holder.name.text = playlistList[position].name
        holder.name.isSelected = true
        holder.count.text =   MyMusicFragment.musicPlaylist.ref[position].playlist.size.toString() + " Songs"
        if (addToPlaylist) {
            holder.root.setOnClickListener {
                music?.let { it1 -> add(it1) }
            }
        }
        else {
            holder.root.setOnClickListener {
                val intent = Intent(context, PlaylistDetailsActivity::class.java)
                intent.putExtra("index", position)
                ContextCompat.startActivity(context, intent, null)
            }
        }


        if (MyMusicFragment.musicPlaylist.ref[position].playlist.size > 0) {
            Glide.with(context)
                .load(MyMusicFragment.musicPlaylist.ref[position].playlist[0].image)
                .apply(
                    RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen)
                        .centerCrop()
                )
                .into(holder.image)
        }

    }

    override fun getItemCount(): Int {
        return playlistList.size
    }

    fun refreshPlaylist() {
        playlistList = ArrayList()
        playlistList.addAll(MyMusicFragment.musicPlaylist.ref)
        notifyDataSetChanged()
    }

    private fun add(data: Data) {

        MyMusicFragment.musicPlaylist.ref[PlaylistDetailsActivity.currentPlaylistPos].playlist.forEachIndexed { index, music ->
            if (data.id == music.id) {
                MyMusicFragment.musicPlaylist.ref[PlaylistDetailsActivity.currentPlaylistPos].playlist.removeAt(
                    index
                )
            }

        }
        MyMusicFragment.musicPlaylist.ref[PlaylistDetailsActivity.currentPlaylistPos].playlist.add(
            data
        )
        adapter = MusicAdapter(
            context,
            MyMusicFragment.musicPlaylist.ref[PlaylistDetailsActivity.currentPlaylistPos].playlist
        )
        MainActivity.musicListSearch.add(data)
        adapter.updateMusicList(MainActivity.musicListSearch)
        Toast.makeText(context, "Song added successfully!!", Toast.LENGTH_SHORT).show()

    }
}


