package com.ddona.music_download_ms3_tunk.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ddona.music_download_ms3_tunk.R
import com.ddona.music_download_ms3_tunk.callback.ListSongItemClick
import com.ddona.music_download_ms3_tunk.callback.ListenedSongItemClick
import com.ddona.music_download_ms3_tunk.databinding.CreateNewPlaylistDialogBinding
import com.ddona.music_download_ms3_tunk.databinding.ItemTopListenedBinding
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.model.favouriteCheckerID
import com.ddona.music_download_ms3_tunk.model.playlistMusic
import com.ddona.music_download_ms3_tunk.ui.activity.MainActivity
import com.ddona.music_download_ms3_tunk.user_case.UseCases
import com.ddona.music_download_ms3_tunk.utils.startDownloadSong
import com.example.newsapp.adapter.diffutil.SongDiffCallback
import com.example.newsapp.fragments.FavouriteFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.*


private lateinit var bottomSheet2: BottomSheetDialog
private lateinit var music2: Data
private lateinit var adapter: PlaylistAdapter

class TopListAdapter(
    private val context: Context,
    private val callback: ListenedSongItemClick,
    private val userCases: UseCases,
    private val seeAll: Boolean = true
) :
    ListAdapter<Data, TopListAdapter.ViewHolder>(
        SongDiffCallback()
    ), ListSongItemClick {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        adapter = PlaylistAdapter(context, userCases, true, this, null)

        CoroutineScope(Dispatchers.IO).launch {
            userCases.getAllPlaylist().collect {
                withContext(Dispatchers.Main) {
                    adapter.submitList(it)
                }
            }
        }


        bottomSheet2 = BottomSheetDialog(context, R.style.BottomSheetStyle)
        bottomSheet2.setContentView(R.layout.bottom_sheet_add_playlist)
        val binder = bottomSheet2.findViewById<RecyclerView>(R.id.rv_new_playlist)
        binder?.setHasFixedSize(true)
        binder?.adapter = adapter


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


    fun showBottomSheetDialogAdapter(data: Data) {
        music2 = data
        val bottomSheet =
            BottomSheetDialog(context)
        bottomSheet.setContentView(R.layout.add_option_dialog)


        val addPlaylist = bottomSheet.findViewById<View>(R.id.ln_add_to_playlist)
        val addFavorite = bottomSheet.findViewById<View>(R.id.ln_add_to_favorite)
        val addDownload = bottomSheet.findViewById<View>(R.id.ln_add_to_download)
        val addShareSong = bottomSheet.findViewById<View>(R.id.ln_share_song)


        addShareSong?.visibility = View.VISIBLE

        addShareSong?.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, data.audio)
            startActivity(context, Intent.createChooser(shareIntent, "Sharing Music File!!"), null)
        }
        
        addPlaylist?.setOnClickListener()

        {

            val createPLBtn = bottomSheet2.findViewById<View>(R.id.ln_create_playlist)
            val searchText =
                bottomSheet2.findViewById<EditText>(R.id.edt_search)

            var job: Job? = null

            searchText?.addTextChangedListener { editable ->
                job?.cancel()
                job = MainScope().launch {
                    editable?.let {
                        if (editable.toString().isNotEmpty()) {
                            CoroutineScope(Dispatchers.IO).launch {
                                userCases.getAllPlaylistByName.invoke(editable.toString()).collect {
                                    adapter.submitList(it)
                                }
                            }
                        } else {
                            CoroutineScope(Dispatchers.IO).launch {
                                userCases.getAllPlaylist().collect {
                                    adapter.submitList(it)
                                }
                            }
                        }
                    }
                }
            }

            createPLBtn?.setOnClickListener {
                customAlertDialogAdapter()
            }

            bottomSheet2.show()

        }

        val fIndex = favouriteCheckerID(data.id)
        val txtAddFavorite = bottomSheet.findViewById<TextView>(R.id.txt_add_to_favorite)

        if (fIndex != -1) txtAddFavorite?.text = "Remove from Favorite"
        else txtAddFavorite?.text = "Add to Favorite"

        addFavorite?.setOnClickListener()
        {
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
        addDownload?.setOnClickListener()
        {
            if(MainActivity.permssion == 1){
                startDownloadSong(data.audio,context)
            }

            else{

                Toast.makeText(context,"Permission Denied",Toast.LENGTH_LONG).show()
            }

            bottomSheet.dismiss()
        }

        bottomSheet.show()
    }


    private fun customAlertDialogAdapter() {
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
                    addPlaylist(playlistName.toString(), context)

                }
            dialog.dismiss()
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setGravity(Gravity.TOP)

    }

    fun addPlaylist(name: String, context: Context) {
        var playlistExists = false
        for (i in MainActivity.playlistList) {
            if (name == i.playlistName) {
                playlistExists = true
                break
            }
        }
        

        if (playlistExists) Toast.makeText(context, "Playlist Exist!!", Toast.LENGTH_SHORT).show()
        else {

            val tempPlaylist = playlistMusic(
                playlistName = name, playList_ID = null
            )

            CoroutineScope(Dispatchers.IO).launch {
                userCases.addPlaylist.invoke(tempPlaylist)
            }

            val customDialogsuccess = LayoutInflater.from(context)
                .inflate(R.layout.create_playlist_successfully, MainActivity.binding.root, false)
            val build = MaterialAlertDialogBuilder(context)
            val dialogsuccess = build.setView(customDialogsuccess)
                .create()


            dialogsuccess.show()
            dialogsuccess.window?.setGravity(Gravity.TOP)

            MainScope().launch {
                delay(1000)
                dialogsuccess.dismiss()
            }


        }
    }


    override fun ListSongOnClick(index: Int) {
        val music = Data(
            id = music2.id,
            albumId = music2.albumId,
            albumName = music2.albumName,
            artistId = music2.artistId,
            audio = music2.audio,
            artistName = music2.artistName,
            duration = music2.duration,
            image = music2.image,
            name = music2.name,
            playlist_id = index,
        )
        CoroutineScope(Dispatchers.IO).launch {

            userCases.addMusic(music)
        }

        Toast.makeText(context, "Song Added successfully!!", Toast.LENGTH_SHORT).show()

    }

}