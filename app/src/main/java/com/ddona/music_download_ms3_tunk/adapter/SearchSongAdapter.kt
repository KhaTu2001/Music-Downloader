package com.ddona.music_download_ms3_tunk.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ddona.music_download_ms3_tunk.R
import com.ddona.music_download_ms3_tunk.databinding.ItemTopListenedBinding
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.ui.fragment.SelectionFragment
import com.ddona.music_download_ms3_tunk.user_case.UseCases
import com.ddona.music_download_ms3_tunk.utils.customDialogsuccess
import com.example.newsapp.adapter.diffutil.SongDiffCallback
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect


class SearchSongAdapter(
    var playlist_ID: Int,
    var userCases: UseCases,
    var context: Context

) : ListAdapter<Data, SearchSongAdapter.ViewHolder>(
    SongDiffCallback()
) {

    companion object {
        var listSongSelected: ArrayList<Data> = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTopListenedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private var binding: ItemTopListenedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Data) {
            var isAdded = false
            data.playlist_id = playlist_ID
            Glide.with(binding.songImg)
                .load(data.image)
                .into(binding.songImg)
            binding.songNamed.text = data.name
            binding.songAuthor.text = data.artistName
            binding.lnIndex.visibility = View.GONE
            binding.songOption.visibility = View.GONE
            binding.songAdded.visibility = View.VISIBLE

            CoroutineScope(Dispatchers.IO).launch {
                userCases.checkSongID.invoke(playlist_ID, data.id).collect {
                    var check = it
                    Log.d("dfdsf", " $check")

                    withContext(Dispatchers.Main) {
                        if (check > 0) {
                            isAdded = true
                            binding.songAdded.setImageResource(R.drawable.ic_remove_to_selectlist)

                        }
                    }
                }
            }

            binding.songAdded.setOnClickListener {
                if (isAdded) {
                    isAdded = false
                    binding.songAdded.setImageResource(R.drawable.ic_add_to_selectlist)
                    removeSongFromList(data)
                    SelectionFragment.binding.btnAdd.text = "Added (${listSongSelected.size})"
                    customDialogsuccess(context,"Remove the song from playlist ")
                } else {
                    isAdded = true
                    binding.songAdded.setImageResource(R.drawable.ic_remove_to_selectlist)
                    addSongToList(data)
                    SelectionFragment.binding.btnAdd.text = "Added (${listSongSelected.size})"
                    customDialogsuccess(context,"Added the song to the playlist")

                }
            }
        }
    }

    fun addSongToList(data: Data) {
        if (data in listSongSelected) {
            listSongSelected = listSongSelected

        } else {
            listSongSelected.add(data)

        }
    }

    fun removeSongFromList(data: Data) {
        for (i in listSongSelected.indices) {
            if (listSongSelected[i] == data) {
                listSongSelected.removeAt(i)

                break
            }
        }
    }


}