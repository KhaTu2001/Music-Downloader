package com.ddona.music_download_ms3_tunk.ui.fragment

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.ddona.music_download_ms3_tunk.R
import com.ddona.music_download_ms3_tunk.adapter.PlaylistAdapter
import com.ddona.music_download_ms3_tunk.databinding.CreateNewPlaylistDialogBinding
import com.ddona.music_download_ms3_tunk.databinding.FragmentMyMusicBinding
import com.ddona.music_download_ms3_tunk.model.playlistMusic
import com.ddona.music_download_ms3_tunk.user_case.UseCases
import com.ddona.music_download_ms3_tunk.viewmodel.SongViewModel
import com.example.newsapp.fragments.FavouriteFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject


@AndroidEntryPoint
class MyMusicFragment : Fragment() {
    @Inject
    lateinit var usercase: UseCases

    val viewModel: SongViewModel by activityViewModels()

    companion object{
        lateinit var binding: FragmentMyMusicBinding
        var indexOF:Int = 0

    }

    private lateinit var adapter: PlaylistAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMyMusicBinding.inflate(inflater)
        binding.fvCountSong.text = FavouriteFragment.favouriteList.size.toString() + " Songs"

        binding.rvPlaylist.setHasFixedSize(true)

        adapter = PlaylistAdapter(requireContext(),usercase,false,null)

        lifecycleScope.launch(Dispatchers.IO){
            viewModel.getAllPlaylist().collect {
                withContext(Dispatchers.Main) {
                    adapter.submitList(it)

                    binding.shimmerViewContainer.stopShimmerAnimation()
                    binding.shimmerViewContainer.visibility = View.GONE
                    binding.rvPlaylist.visibility = View.VISIBLE
                }
            }
        }
        binding.rvPlaylist.adapter = adapter


        binding.lnAddToFavorite.setOnClickListener {
            val action = PlaylistFragmentDirections.actionPlaylistFragmentToFavouriteFragment()
            Navigation.findNavController(binding.root).navigate(action)
        }

        binding.lnCreatePlaylist.setOnClickListener {
            customAlertDialog()
        }
        return binding.root
    }

    private fun customAlertDialog() {
        val customDialog = LayoutInflater.from(context)
            .inflate(R.layout.create_new_playlist_dialog, binding.root, false)
        val binder = CreateNewPlaylistDialogBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(requireContext(), R.style.full_screen_dialog)
        val dialog = builder.setView(customDialog)
            .create()


        val playlistName = binder.edtNamePlaylist.text
        val submitBtn = binder.createPlaylistBtn
        val cancelBtn = binder.cancelBtn
        submitBtn.setOnClickListener {
            if (playlistName != null)
                if (playlistName.isNotEmpty()) {
                    addPlaylist(playlistName.toString())
                }
            dialog.dismiss()
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun addPlaylist(name: String) {
        var playlistExists = false
        for (i in PlaylistAdapter.playlistList) {
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

            lifecycleScope.launch {
                usercase.addPlaylist.invoke(tempPlaylist)
            }


            val customDialogsuccess = LayoutInflater.from(context)
                .inflate(R.layout.create_playlist_successfully, binding.root, false)
            val build = MaterialAlertDialogBuilder(requireContext(), R.style.full_screen_dialog)
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


    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()

        binding.shimmerViewContainer.startShimmerAnimation()
        if (binding.shimmerViewContainer.visibility == View.GONE)
            binding.rvPlaylist.visibility = View.VISIBLE
    }



}





