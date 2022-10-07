package com.ddona.music_download_ms3_tunk.adapter

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ddona.music_download_ms3_tunk.R
import com.ddona.music_download_ms3_tunk.callback.ListenedSongItemClick
import com.ddona.music_download_ms3_tunk.databinding.CreateNewPlaylistDialogBinding
import com.ddona.music_download_ms3_tunk.databinding.ItemTopListenedBinding
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.model.Playlist
import com.ddona.music_download_ms3_tunk.model.favouriteCheckerID
import com.ddona.music_download_ms3_tunk.ui.activity.MainActivity
import com.ddona.music_download_ms3_tunk.ui.fragment.MyMusicFragment
import com.example.newsapp.adapter.diffutil.SongDiffCallback
import com.example.newsapp.fragments.FavouriteFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private lateinit var adapter: PlaylistViewAdapter
private lateinit var adapter2: MusicAdapter
private lateinit var bottomSheet2: BottomSheetDialog


class TopListAdapter(
    private val context: Context,
    private val callback: ListenedSongItemClick,
    private val seeAll: Boolean = true
) :
    ListAdapter<Data, TopListAdapter.ViewHolder>(
        SongDiffCallback()
    ) {


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
            if (data.audio != null) {
                Glide.with(binding.songImg)
                    .load(data.image)
                    .into(binding.songImg)
                binding.songNamed.text = data.name
                binding.songAuthor.text = data.artistName
                binding.itemsSong.setOnClickListener {

                    callback.ListOnClick(adapterPosition)
                }

                binding.songOption.setOnClickListener {
                    showBottomSheetDialogAdapter(data)
                }
                binding.numericalOrder.text = (adapterPosition + 1).toString() + "."

                if (adapterPosition == 0) {
                    binding.numericalOrder.setTextColor(Color.parseColor("#F89500"))
                }
                if (adapterPosition == 1) {
                    binding.numericalOrder.setTextColor(Color.parseColor("#00FF57"))

                }
                if (adapterPosition == 2) {
                    binding.numericalOrder.setTextColor(Color.parseColor("#FF1C1C"))
                }
                if (adapterPosition == 3 && seeAll) {
                    binding.numericalOrder.setTextColor(Color.parseColor("#F500BF"))
                }
            }
        }
    }


    fun showBottomSheetDialogAdapter(data: Data) {
        val bottomSheet: BottomSheetDialog =
            BottomSheetDialog(context, R.style.BottomSheetStyle)
        bottomSheet.setContentView(R.layout.add_option_dialog)


        adapter = PlaylistViewAdapter(
            context,
            playlistList = MyMusicFragment.musicPlaylist.ref,
            true,
            data
        )

        bottomSheet2 = BottomSheetDialog(context)
        bottomSheet2.setContentView(R.layout.bottom_sheet_add_playlist)
        val binder = bottomSheet2.findViewById<RecyclerView>(R.id.rv_new_playlist)
        binder?.setHasFixedSize(true)
        binder?.adapter = adapter


        val addPlaylist = bottomSheet.findViewById<View>(R.id.ln_add_to_playlist)
        val addFavorite = bottomSheet.findViewById<View>(R.id.ln_add_to_favorite)
        val addDownload = bottomSheet.findViewById<View>(R.id.ln_add_to_download)
        val addShareSong = bottomSheet.findViewById<View>(R.id.ln_share_song)


        addShareSong?.visibility = View.VISIBLE

        addPlaylist?.setOnClickListener()

        {

            val createPLBtn = bottomSheet2.findViewById<View>(R.id.ln_create_playlist)

            createPLBtn?.setOnClickListener {
                customAlertDialogAdapter(data)
            }
            bottomSheet2.dismiss()
            bottomSheet2.show()
        }

        var fIndex = favouriteCheckerID(data.id)
        var txtAddFavorite = bottomSheet.findViewById<TextView>(R.id.txt_add_to_favorite)

        if (fIndex != -1) txtAddFavorite?.setText("Remove from Favorite")
        else txtAddFavorite?.setText("Add to Favorite")

        addFavorite?.setOnClickListener()
        {


            if (fIndex != -1) {
                FavouriteFragment.favouriteList.removeAt(fIndex)

            } else {
                FavouriteFragment.favouriteList.add(data)

            }
            FavouriteFragment.favouritesChanged = true
            bottomSheet.dismiss()
        }
        addDownload?.setOnClickListener()
        {

            bottomSheet.dismiss()
        }

        bottomSheet.show()
    }


    fun customAlertDialogAdapter(data: Data) {
        val customDialog = LayoutInflater.from(context)
            .inflate(R.layout.create_new_playlist_dialog, MainActivity.binding.root, false)
        val binder = CreateNewPlaylistDialogBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(context)
        val dialog = builder.setView(customDialog)
            .create()
        val playlistName = binder.edtNamePlaylist.text
        val submitBtn = binder.createPlaylistBtn
        val cancelBtn = binder.cancelBtn



        submitBtn.setOnClickListener {
            if (playlistName != null)
                if (playlistName.isNotEmpty()) {
                    addPlaylist(playlistName.toString(), context, data)

                }
            dialog.dismiss()
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.setCanceledOnTouchOutside(false);
        dialog.window?.setGravity(Gravity.TOP)

    }

    fun addPlaylist(name: String, context: Context, data: Data) {
        var playlistExists = false
        for (i in MyMusicFragment.musicPlaylist.ref) {
            if (name == i.name) {
                playlistExists = true
                break
            }
        }

        val customDialogsuccess = LayoutInflater.from(context)
            .inflate(R.layout.create_playlist_successfully, MainActivity.binding.root, false)
        val build = MaterialAlertDialogBuilder(context)
        val dialogsuccess = build.setView(customDialogsuccess)
            .create()


        if (playlistExists) Toast.makeText(context, "Playlist Exist!!", Toast.LENGTH_SHORT)
            .show()
        else {
            adapter = PlaylistViewAdapter(
                context,
                playlistList = MyMusicFragment.musicPlaylist.ref,
                false,
                data
            )
            val tempPlaylist = Playlist()
            tempPlaylist.name = name
            tempPlaylist.playlist = ArrayList()
            MyMusicFragment.musicPlaylist.ref.add(tempPlaylist)
            adapter.refreshPlaylist()

            dialogsuccess.show()
            dialogsuccess.window?.setGravity(Gravity.TOP)


            MainScope().launch {
                delay(2000)
                dialogsuccess.dismiss()
            }
        }
    }


}