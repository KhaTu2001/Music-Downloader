package com.ddona.music_download_ms3_tunk.adapter

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ddona.music_download_ms3_tunk.R
import com.ddona.music_download_ms3_tunk.callback.ListSongItemClick
import com.ddona.music_download_ms3_tunk.databinding.CreateNewPlaylistDialogBinding
import com.ddona.music_download_ms3_tunk.databinding.ItemTopListenedBinding
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.model.playlistMusic
import com.ddona.music_download_ms3_tunk.ui.activity.MainActivity
import com.ddona.music_download_ms3_tunk.ui.activity.PlayerActivity
import com.ddona.music_download_ms3_tunk.user_case.UseCases
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.*
import java.io.File
import java.util.*


private lateinit var bottomSheet2: BottomSheetDialog
private lateinit var music2: Data
private lateinit var adapter: PlaylistAdapter


class MusicAdapter(
    private val context: Context,
    private var musicList: ArrayList<Data>,
    private val userCases: UseCases?,


    ) : RecyclerView.Adapter<MusicAdapter.MyHolder>(), ListSongItemClick {

    class MyHolder(binding: ItemTopListenedBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.songNamed
        val article = binding.songAuthor
        val image = binding.songImg
        val index = binding.numericalOrder
        val option = binding.songOption
        val root = binding.itemsSong
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {


        adapter = userCases?.let { PlaylistAdapter(context, it, true, this, null) }!!

        CoroutineScope(Dispatchers.IO).launch {
            userCases.getAllPlaylist.invoke(1).collect {
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


        return MyHolder(ItemTopListenedBinding.inflate(LayoutInflater.from(context), parent, false))

    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.title.text = musicList[position].name
        holder.article.text = musicList[position].artistName
        holder.index.visibility = View.GONE

        Glide.with(context)
            .load(musicList[position].image)
            .apply(
                RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).centerCrop()
            )
            .into(holder.image)

        holder.root.setOnClickListener {
            sendIntent(ref = "MusicAdapter", pos = position)
        }

        holder.option.setOnClickListener {
            showBottomSheetDialogAdapter(musicList[position],position)
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

    fun showBottomSheetDialogAdapter(data: Data,position: Int) {
        music2 = data

        val bottomSheet = BottomSheetDialog(context)
        bottomSheet.setContentView(R.layout.add_option_dialog)


        val addPlaylist = bottomSheet.findViewById<View>(R.id.ln_add_to_playlist)
        val addFavorite = bottomSheet.findViewById<View>(R.id.ln_add_to_favorite)
        val addDownload = bottomSheet.findViewById<View>(R.id.ln_add_to_download)
        val addShareSong = bottomSheet.findViewById<View>(R.id.ln_share_song)
        val deleteSong = bottomSheet.findViewById<View>(R.id.ln_remove_download)
        val setRingtone = bottomSheet.findViewById<View>(R.id.ln_set_ringtone_download)

        addDownload?.visibility = View.GONE
        addFavorite?.visibility = View.GONE
        deleteSong?.visibility = View.VISIBLE
        setRingtone?.visibility = View.VISIBLE



        addShareSong?.visibility = View.VISIBLE

        addShareSong?.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, music2.audio)
            ContextCompat.startActivity(
                context,
                Intent.createChooser(shareIntent, "Sharing Music File!!"),
                null
            )
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
                                userCases?.getAllPlaylistByName?.invoke(editable.toString(), 1)
                                    ?.collect {
                                        adapter.submitList(it)
                                    }
                            }
                        } else {
                            CoroutineScope(Dispatchers.IO).launch {
                                userCases?.getAllPlaylist?.let { it1 -> it1(1) }?.collect {
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

        setRingtone?.setOnClickListener {

            val filepath = "/sdcard/sample/" + data.audio + ""
            val k = File( filepath) // path is a file playing


            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DATA, k.absolutePath)
            values.put(MediaStore.MediaColumns.SIZE, k.length())
            values.put(MediaStore.Audio.Media.ARTIST, data.artistName)
            values.put(MediaStore.Audio.Media.TITLE, data.name)
            values.put(MediaStore.Audio.Media.DURATION, data.duration)
            values.put(MediaStore.Audio.Media.IS_RINGTONE, true)
            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true)
            values.put(MediaStore.Audio.Media.IS_ALARM, true)
            values.put(MediaStore.Audio.Media.IS_MUSIC, false)




            val newUri: Uri = context.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)!!

            MainActivity.RingtoneUtils.setRingtone(context, newUri,RingtoneManager.TYPE_RINGTONE);

            bottomSheet.dismiss()
        }

        deleteSong?.setOnClickListener {

            bottomSheet.dismiss()
        }
    bottomSheet.show()

}


fun customAlertDialogAdapter() {
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
    for (i in MainActivity.playlistListOff) {
        if (name == i.playlistName) {
            playlistExists = true
            break
        }
    }



    if (playlistExists) Toast.makeText(context, "Playlist Exist!!", Toast.LENGTH_SHORT).show()
    else {

        val tempPlaylist = playlistMusic(
            playlistName = name, playList_ID = null, status = 1
        )

        CoroutineScope(Dispatchers.IO).launch {
            userCases!!.addPlaylist.invoke(tempPlaylist)
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
        audioDownload = music2.audioDownload,
        albumId = music2.albumId,
        albumName = music2.albumName,
        artistId = music2.artistId,
        audio = music2.audio,
        artistName = music2.artistName,
        duration = music2.duration,
        image = music2.image,
        name = music2.name,
        playlist_id = index,
        status = 1
    )
    runBlocking {
        launch {
            userCases?.addMusic?.let { it(music) }
        }
    }

    Toast.makeText(context, "Song Added successfully!!", Toast.LENGTH_SHORT).show()

}


}