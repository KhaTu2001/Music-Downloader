//package com.ddona.music_download_ms3_tunk.utils
//
//import android.content.Context
//import android.view.Gravity
//import android.view.LayoutInflater
//import android.view.View
//import android.widget.SearchView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.recyclerview.widget.RecyclerView
//import com.ddona.music_download_ms3_tunk.R
//import com.ddona.music_download_ms3_tunk.adapter.PlaylistViewAdapter
//import com.ddona.music_download_ms3_tunk.adapter.TopListAdapter
//import com.ddona.music_download_ms3_tunk.databinding.CreateNewPlaylistDialogBinding
//import com.ddona.music_download_ms3_tunk.model.Data
//import com.ddona.music_download_ms3_tunk.model.Playlist
//import com.ddona.music_download_ms3_tunk.model.favouriteCheckerID
//import com.ddona.music_download_ms3_tunk.ui.activity.MainActivity
//import com.ddona.music_download_ms3_tunk.ui.fragment.MyMusicFragment
//import com.example.newsapp.fragments.FavouriteFragment
//import com.google.android.material.bottomsheet.BottomSheetDialog
//import com.google.android.material.dialog.MaterialAlertDialogBuilder
//import kotlinx.coroutines.MainScope
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.runBlocking
//
//
//private lateinit var adapter:TopListAdapter
//private lateinit var adapter2:PlaylistViewAdapter
//private lateinit var bottomSheet2: BottomSheetDialog
//private lateinit var music2: Data
//private var playListSearch: ArrayList<Playlist> = ArrayList()
//
//
//fun showBottomSheetDialogAdapter(context: Context,data: Data) {
//    music2 = data
//    val bottomSheet: BottomSheetDialog =
//        BottomSheetDialog(context, R.style.BottomSheetStyle)
//    bottomSheet.setContentView(R.layout.add_option_dialog)
//
//
//    val addPlaylist = bottomSheet.findViewById<View>(R.id.ln_add_to_playlist)
//    val addFavorite = bottomSheet.findViewById<View>(R.id.ln_add_to_favorite)
//    val addDownload = bottomSheet.findViewById<View>(R.id.ln_add_to_download)
//    val addShareSong = bottomSheet.findViewById<View>(R.id.ln_share_song)
//
//
//    addShareSong?.visibility = View.VISIBLE
//
//    addPlaylist?.setOnClickListener()
//
//    {
//        bottomSheet2 = BottomSheetDialog(context, R.style.BottomSheetStyle)
//        bottomSheet2.setContentView(R.layout.bottom_sheet_add_playlist)
//        val binder = bottomSheet2.findViewById<RecyclerView>(R.id.rv_new_playlist)
//        binder?.setHasFixedSize(true)
//        binder?.adapter = adapter
//
//        val createPLBtn = bottomSheet2.findViewById<View>(R.id.ln_create_playlist)
//        val searchText = bottomSheet2.findViewById<SearchView>(R.id.edt_search_playlist)
//
//        createPLBtn?.setOnClickListener {
//            customAlertDialogAdapter(context,data)
//
//            searchText?.setOnQueryTextListener(object :
//                androidx.appcompat.widget.SearchView.OnQueryTextListener,
//                SearchView.OnQueryTextListener {
//                override fun onQueryTextSubmit(query: String?): Boolean = true
//                override fun onQueryTextChange(newText: String?): Boolean {
//                    playListSearch = ArrayList()
//                    if (newText != null) {
//                        val userInput = newText.lowercase()
//                        for (playlist in MyMusicFragment.musicPlaylist.ref)
//                            if (playlist.name.lowercase().contains(userInput))
//                                playListSearch.add(playlist)
//                        adapter2?.updatePlaylist(playListSearch)
//                    }
//                    return true
//
//                }
//
//            })
//            bottomSheet2.dismiss()
//        }
//        bottomSheet2.show()
//    }
//
//    var fIndex = favouriteCheckerID(data.id)
//    var txtAddFavorite = bottomSheet.findViewById<TextView>(R.id.txt_add_to_favorite)
//
//    if (fIndex != -1) txtAddFavorite?.setText("Remove from Favorite")
//    else txtAddFavorite?.setText("Add to Favorite")
//
//    addFavorite?.setOnClickListener()
//    {
//        if (fIndex != -1) {
//            FavouriteFragment.favouriteList.removeAt(fIndex)
//            Toast.makeText(
//                context,
//                "Song Remove from Favourite successfully!!",
//                Toast.LENGTH_SHORT
//            ).show()
//
//
//        } else {
//            FavouriteFragment.favouriteList.add(data)
//            Toast.makeText(
//                context,
//                "Song added to Favourite successfully!!",
//                Toast.LENGTH_SHORT
//            ).show()
//
//
//        }
//        FavouriteFragment.favouritesChanged = true
//        bottomSheet.dismiss()
//    }
//    addDownload?.setOnClickListener()
//    {
//
//        bottomSheet.dismiss()
//    }
//
//    bottomSheet.show()
//}
//
//
//fun customAlertDialogAdapter(context: Context,data: Data) {
//    val customDialog = LayoutInflater.from(context)
//        .inflate(R.layout.create_new_playlist_dialog, MainActivity.binding.root, false)
//    val binder = CreateNewPlaylistDialogBinding.bind(customDialog)
//    val builder = MaterialAlertDialogBuilder(context)
//    val dialog = builder.setView(customDialog)
//        .create()
//    val playlistName = binder.edtNamePlaylist.text
//    val submitBtn = binder.createPlaylistBtn
//    val cancelBtn = binder.cancelBtn
//
//
//
//    submitBtn.setOnClickListener {
//        if (playlistName != null)
//            if (playlistName.isNotEmpty()) {
//                addPlaylist(playlistName.toString(), context)
//
//            }
//        dialog.dismiss()
//    }
//
//    cancelBtn.setOnClickListener {
//        dialog.dismiss()
//    }
//
//    dialog.show()
//    dialog.setCanceledOnTouchOutside(false);
//    dialog.window?.setGravity(Gravity.TOP)
//
//}
//
//fun addPlaylist(name: String, context: Context) {
//    var playlistExists = false
//    for (i in MyMusicFragment.musicPlaylist.ref) {
//        if (name == i.name) {
//            playlistExists = true
//            break
//        }
//    }
//
//    val customDialogsuccess = LayoutInflater.from(context)
//        .inflate(R.layout.create_playlist_successfully, MainActivity.binding.root, false)
//    val build = MaterialAlertDialogBuilder(context)
//    val dialogsuccess = build.setView(customDialogsuccess)
//        .create()
//
//
//    if (playlistExists) Toast.makeText(context, "Playlist Exist!!", Toast.LENGTH_SHORT)
//        .show()
//    else {
//        val tempPlaylist = Playlist()
//        tempPlaylist.name = name
//        tempPlaylist.playlist = ArrayList()
//        MyMusicFragment.musicPlaylist.ref.add(tempPlaylist)
//        adapter2?.refreshPlaylist()
//
//        dialogsuccess.show()
//        dialogsuccess.window?.setGravity(Gravity.TOP)
//
//
//        MainScope().launch {
//            delay(2000)
//            dialogsuccess.dismiss()
//        }
//    }
//}
//
//
//override fun ListOnClick(index: Int) {
//
//    val music = Data(
//        id = music2.id,
//        albumId = music2.albumId,
//        albumName = music2.albumName,
//        artistId = music2.artistId,
//        audio = music2.audio,
//        artistName = music2.artistName,
//        audioDownload = music2.audioDownload,
//        duration = music2.duration,
//        image = music2.image,
//        name = music2.name,
//        playlist_id = index,
//    )
//    runBlocking {
//        launch {
//            useCases.addMusic(music)
//        }
//    }
//
//    Toast.makeText(context, "Song Added successfully!!", Toast.LENGTH_SHORT).show()
//
//}