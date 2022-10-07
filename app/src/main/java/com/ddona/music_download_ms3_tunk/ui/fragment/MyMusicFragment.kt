package com.ddona.music_download_ms3_tunk.ui.fragment

import android.os.Bundle

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.ddona.music_download_ms3_tunk.R
import com.ddona.music_download_ms3_tunk.adapter.PlaylistViewAdapter
import com.ddona.music_download_ms3_tunk.callback.SongItemClick
import com.ddona.music_download_ms3_tunk.databinding.CreateNewPlaylistDialogBinding
import com.ddona.music_download_ms3_tunk.databinding.FragmentMyMusicBinding
import com.ddona.music_download_ms3_tunk.model.MusicPlaylist
import com.ddona.music_download_ms3_tunk.model.Playlist
import com.example.newsapp.fragments.FavouriteFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class MyMusicFragment: Fragment(),SongItemClick{

    private lateinit var binding: FragmentMyMusicBinding
    private lateinit var adapter: PlaylistViewAdapter

    companion object{
        var musicPlaylist: MusicPlaylist = MusicPlaylist()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMyMusicBinding.inflate(inflater)
        binding.fvCountSong.text = FavouriteFragment.favouriteList.size.toString() + " Songs"
        binding.lnAddToFavorite.setOnClickListener{
//            val intent = Intent(context, FavouriteFragment::class.java)

        }
        binding.rvPlaylist.setHasFixedSize(true)
        adapter = PlaylistViewAdapter(requireContext(), playlistList = musicPlaylist.ref,false,null)
        binding.rvPlaylist.adapter = adapter

        binding.lnAddToFavorite.setOnClickListener {
            val action = PlaylistFragmentDirections.actionPlaylistFragmentToFavouriteFragment()
            Navigation.findNavController(binding.root).navigate(action)
        }

        binding.lnCreatePlaylist.setOnClickListener{
            customAlertDialog()
        }
        return binding.root
    }

    private fun customAlertDialog() {
        val customDialog = LayoutInflater.from(context)
            .inflate(R.layout.create_new_playlist_dialog, binding.root, false)
        val binder = CreateNewPlaylistDialogBinding.bind(customDialog)
        val builder = context?.let { MaterialAlertDialogBuilder(it) }
        val dialog = builder?.setView(customDialog)
            ?.create()
        val playlistName = binder.edtNamePlaylist.text
        val submitBtn = binder.createPlaylistBtn
        val cancelBtn = binder.cancelBtn
        submitBtn.setOnClickListener {
            if (playlistName != null)
                if (playlistName.isNotEmpty()) {
                    addPlaylist(playlistName.toString())
                }
            dialog?.dismiss()
        }

        cancelBtn.setOnClickListener {
            dialog?.dismiss()
        }

        dialog?.show()

    }

    private fun addPlaylist(name: String){
        var playlistExists = false
        for(i in musicPlaylist.ref) {
            if (name == i.name){
                playlistExists = true
                break
            }
        }
        if(playlistExists) Toast.makeText(context, "Playlist Exist!!", Toast.LENGTH_SHORT).show()
        else {
            val tempPlaylist = Playlist()
            tempPlaylist.name = name
            tempPlaylist.playlist = ArrayList()
            musicPlaylist.ref.add(tempPlaylist)
            adapter.refreshPlaylist()
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    override fun addToPlaylist() {
        TODO("Not yet implemented")
    }
}





