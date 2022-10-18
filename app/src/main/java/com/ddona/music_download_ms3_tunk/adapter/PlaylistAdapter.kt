package com.ddona.music_download_ms3_tunk.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ddona.music_download_ms3_tunk.R
import com.ddona.music_download_ms3_tunk.callback.ListSongItemClick
import com.ddona.music_download_ms3_tunk.databinding.DeletePlaylistDialogBinding
import com.ddona.music_download_ms3_tunk.databinding.ItemPlaylistBinding
import com.ddona.music_download_ms3_tunk.databinding.RenamePlaylistDialogBinding
import com.ddona.music_download_ms3_tunk.model.playlistMusic
import com.ddona.music_download_ms3_tunk.ui.activity.PlaylistDetailsActivity
import com.ddona.music_download_ms3_tunk.user_case.UseCases
import com.example.newsapp.adapter.diffutil.PlaylistDiffCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.*
import androidx.recyclerview.widget.ListAdapter as ListAdapter


class PlaylistAdapter(
    private val context: Context,
    private val useCases: UseCases,
    private val addMusic: Boolean = false,
    private val callback: ListSongItemClick?,


    ) : ListAdapter<playlistMusic, PlaylistAdapter.ViewHolder>(
    PlaylistDiffCallback()
) {
    companion object {
        var playlistList: ArrayList<playlistMusic> = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPlaylistBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))

    }

    inner class ViewHolder(private var binding: ItemPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(playlistMusic: playlistMusic) {


            if (playlistMusic in playlistList) playlistList = playlistList
             else playlistList.add(playlistMusic)


//            if(playlistMusic.playlistName == playlistList[adapterPosition].playlistName) playlistList.removeAt(adapterPosition)

            binding.playlistName.text = playlistMusic.playlistName

            val index: Int? = playlistMusic.playList_ID

            CoroutineScope(Dispatchers.IO).launch {
                index?.let {
                    useCases.countRowSong(it).collect {
                        withContext(Dispatchers.Main) {
                            binding.newCountSong.text = it.toString() + " Songs"
                        }
                    }
                }
            }


            CoroutineScope(Dispatchers.IO).launch {
                index?.let {
                    useCases.getAllMusicByPlaylist(it).collect {
                        withContext(Dispatchers.Main) {
                            if (it.size > 0) {
                                binding.playlistImgInactive.visibility = View.GONE
                                binding.playlistImg.visibility = View.VISIBLE
                                Glide.with(context)
                                    .load(it[0].image)
                                    .into(binding.playlistImg)
                            }

                        }
                    }
                }
            }



            if (addMusic == true) {
                binding.root.setOnClickListener {
                    index?.let { it1 -> callback?.ListSongOnClick(it1) }
                }
            } else {
                binding.root.setOnClickListener {
                    val intent = Intent(context, PlaylistDetailsActivity::class.java)
                    intent.putExtra("index", index)
                    intent.putExtra("playlistName", playlistMusic.playlistName)
                    ContextCompat.startActivity(context, intent, null)
                }
            }


            Log.d("dfgdfdg", "bind: $playlistList")


            binding.playlistOption.setOnClickListener {
                showBottomSheetPlaylist(playlistMusic,binding.root)
            }

        }
    }

    fun showBottomSheetPlaylist(playlistMusic: playlistMusic,viewGroup: ViewGroup) {
        val bottomSheet: BottomSheetDialog =
            BottomSheetDialog(context)
        bottomSheet.setContentView(R.layout.bottomsheet_playlist_option)

        val reNamePlayList = bottomSheet.findViewById<View>(R.id.ln_rename_playlist)
        val reMovePlayList = bottomSheet.findViewById<View>(R.id.ln_remove_playlist)


        reNamePlayList?.setOnClickListener {
            val customDialog = LayoutInflater.from(context)
                .inflate(R.layout.rename_playlist_dialog,viewGroup, false)
            val binder = RenamePlaylistDialogBinding.bind(customDialog)
            val builder = MaterialAlertDialogBuilder(context)
            val dialog = builder.setView(customDialog)
                .create()

            val playlistName = binder.edtNamePlaylist.text
            val submitBtn = binder.acceptActionBtn
            val cancelBtn = binder.cancelActionBtn

            submitBtn.setOnClickListener {
                if (playlistName != null)
                    if (playlistName.isNotEmpty()) {
                        CoroutineScope(Dispatchers.IO).launch {
                            playlistMusic.playList_ID?.let { it1 ->
                                useCases.updatePlaylistUserCase.invoke(
                                    playlistName.toString(),
                                    it1
                                )
                            }
                        }

                    }

                Toast.makeText(
                    context,
                    "Rename playlist to $playlistName successfully!!",
                    Toast.LENGTH_SHORT
                ).show()
                bottomSheet.dismiss()

                dialog.dismiss()
            }

            cancelBtn.setOnClickListener {
                dialog.dismiss()

            }

            dialog.show()
            dialog.setCanceledOnTouchOutside(false);
        }

        reMovePlayList?.setOnClickListener {

            val customDialog = LayoutInflater.from(context)
                .inflate(R.layout.delete_playlist_dialog, viewGroup, false)
            val binder = DeletePlaylistDialogBinding.bind(customDialog)
            val builder = MaterialAlertDialogBuilder(context)
            val dialog = builder.setView(customDialog)
                .create()

            val submitBtn = binder.acceptActionBtn
            val cancelBtn = binder.cancelActionBtn

            submitBtn.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    playlistMusic.playList_ID?.let { it1 ->
                        useCases.deletePlaylistUserCase.invoke(
                            it1
                        )
                    }
                    playlistMusic.playList_ID?.let { it1 -> useCases.deleteMusicUserCase.invoke(it1) }
                }
                dialog.dismiss()
                bottomSheet.dismiss()
                Toast.makeText(context, "Remove playlist successfully!!", Toast.LENGTH_SHORT).show()


            }

            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }


            dialog.show()
            dialog.setCanceledOnTouchOutside(false);
        }


        bottomSheet.show()

    }

}
