package com.ddona.music_download_ms3_tunk.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ddona.music_download_ms3_tunk.R
import com.ddona.music_download_ms3_tunk.databinding.DeletePlaylistDialogBinding
import com.ddona.music_download_ms3_tunk.databinding.ItemTopListenedBinding
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.model.favouriteCheckerID
import com.ddona.music_download_ms3_tunk.ui.activity.PlayerActivity
import com.ddona.music_download_ms3_tunk.ui.fragment.FavouriteFragment
import com.ddona.music_download_ms3_tunk.ui.fragment.MyMusicFragment
import com.ddona.music_download_ms3_tunk.user_case.UseCases
import com.example.newsapp.adapter.diffutil.SongDiffCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PlaylistDetailsAdapter(
    private var context: Context,
    var usercase: UseCases,
    var playLisiID: Int,

    ):ListAdapter<Data,PlaylistDetailsAdapter.ViewHolder>(
    SongDiffCallback()
) {
    var musicList:ArrayList<Data> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemTopListenedBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))

    }
    inner class ViewHolder(private var binding:ItemTopListenedBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Data){
            musicList.add(data)
            Glide.with(binding.songImg)
                .load(data.image)
                .into(binding.songImg)
            binding.songNamed.text = data.name
            binding.songAuthor.text = data.artistName
            binding.lnIndex.visibility = View.GONE

            binding.root.setOnClickListener {
                val intent = Intent(context, PlayerActivity::class.java)
                intent.putExtra("index", adapterPosition)
                intent.putExtra("from", "playlistDetails")
                intent.putExtra("dataMusic",musicList)
                startActivity(context, intent, null)
            }

            binding.songOption.setOnClickListener {
                showBottomSheetPlaylist(data)
            }

        }

    }
    fun showBottomSheetPlaylist(data: Data) {
        val bottomSheet: BottomSheetDialog =
            BottomSheetDialog(context)
        bottomSheet.setContentView(R.layout.bottomsheet_playlist_option)

        val reName = bottomSheet.findViewById<View>(R.id.ln_rename_playlist)
        val reMove = bottomSheet.findViewById<View>(R.id.ln_remove_playlist)
        val removeSongPlayList = bottomSheet.findViewById<View>(R.id.ln_remove_song_playlist)
        val addShareSong = bottomSheet.findViewById<View>(R.id.ln_share_song)
        val setAsRingtone = bottomSheet.findViewById<View>(R.id.ln_set_ringtone_download)
        val addToFavourite = bottomSheet.findViewById<View>(R.id.ln_add_to_favorite)




        reName?.visibility = View.GONE
        reMove?.visibility = View.GONE
        removeSongPlayList?.visibility = View.VISIBLE
        addShareSong?.visibility = View.VISIBLE
        setAsRingtone?.visibility = View.VISIBLE
        addToFavourite?.visibility = View.VISIBLE

        removeSongPlayList?.setOnClickListener {

            val customDialog = LayoutInflater.from(context)
                .inflate(R.layout.delete_playlist_dialog, MyMusicFragment.binding.root, false)
            val binder = DeletePlaylistDialogBinding.bind(customDialog)
            val builder = MaterialAlertDialogBuilder(context)
            val dialog = builder.setView(customDialog)
                .create()

            val title = binder.txtPlaylistTittle
            val content = binder.txtDeletePlaylist

            title.text = data.name
            content.text = "Do You Want To Remove This Song"
            val submitBtn = binder.acceptActionBtn
            val cancelBtn = binder.cancelActionBtn


            submitBtn.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    usercase.deleteMusicFromPlaylistUserCase.invoke(playLisiID,data.id)
                }

                Toast.makeText(context, "Remove Song Successfully!!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()

            }


            cancelBtn.setOnClickListener {
                dialog.dismiss()

            }


            dialog.show()
            dialog.setCanceledOnTouchOutside(false);


        }

        addShareSong?.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_TEXT, data.audio)
            startActivity(context, Intent.createChooser(shareIntent, "Sharing Music File!!"), null)
        }

        setAsRingtone?.setOnClickListener {

        }


        var fIndex = favouriteCheckerID(data.id)
        var txtAddFavorite = bottomSheet.findViewById<TextView>(R.id.txt_add_to_favorite)

        if (fIndex != -1) txtAddFavorite?.setText("Remove from Favorite")
        else txtAddFavorite?.setText("Add to Favorite")

        addToFavourite?.setOnClickListener {
            if (fIndex != -1) {
                FavouriteFragment.favouriteList.removeAt(fIndex)
                Toast.makeText(
                    context,
                    "Song Remove from Favourite successfully!!",
                    Toast.LENGTH_SHORT
                ).show()


            } else {
                FavouriteFragment.favouriteList.add(data)
                Toast.makeText(
                    context,
                    "Song added to Favourite successfully!!",
                    Toast.LENGTH_SHORT
                ).show()


            }
            FavouriteFragment.favouritesChanged = true
            bottomSheet.dismiss()
        }


        bottomSheet.show()

    }


}