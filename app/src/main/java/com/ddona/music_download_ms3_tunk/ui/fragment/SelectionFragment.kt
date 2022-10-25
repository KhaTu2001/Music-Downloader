package com.ddona.music_download_ms3_tunk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ddona.music_download_ms3_tunk.R
import com.ddona.music_download_ms3_tunk.adapter.SearchSongAdapter
import com.ddona.music_download_ms3_tunk.databinding.DeletePlaylistDialogBinding
import com.ddona.music_download_ms3_tunk.databinding.FragmentSelectionBinding
import com.ddona.music_download_ms3_tunk.user_case.UseCases
import com.ddona.music_download_ms3_tunk.viewmodel.SongViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject


@AndroidEntryPoint
class SelectionFragment : Fragment() {

    companion object {
        lateinit var binding: FragmentSelectionBinding

    }

    val viewModel: SongViewModel by activityViewModels()

    private val args: SelectionFragmentArgs by navArgs()

    @Inject
    lateinit var userCase: UseCases

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSelectionBinding.inflate(inflater)
        val from = args.from

        val playlistID = args.currentPlaylist

        val adapter = SearchSongAdapter(playlistID, userCase,requireContext())

        var job: Job? = null
        binding.edtSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.searchSong(editable.toString())
                        binding.txtAdd.text = "Search results for \"$editable \" "
                    } else binding.txtAdd.text = "Recommended for you"
                }
            }
        }

        viewModel.searchList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }


        binding.selectionRV.adapter = adapter

        binding.btnAdd.setOnClickListener {
            addSongToPlaylist()
        }

        binding.exitBtn.setOnClickListener {
            if(from ==1) findNavController().popBackStack()

            else{
                val action = SelectionFragmentDirections.actionSelectionFragmentToPlaylistFragment()
                findNavController().navigate(action)
            }
        }

        return binding.root


    }

    private fun addSongToPlaylist() {


        val customDialog = LayoutInflater.from(context)
            .inflate(R.layout.delete_playlist_dialog, binding.root, false)
        val binder = DeletePlaylistDialogBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(requireContext())
        val dialog = builder.setView(customDialog)
            .create()

        val txtSuccess = binder.txtDeletePlaylist
        txtSuccess.text =
            SearchSongAdapter.listSongSelected.size.toString() + " Songs Will Be Added To The Playlist Soon"
        val submitBtn = binder.acceptActionBtn
        submitBtn.text = "DONE"
        val cancelBtn = binder.cancelActionBtn


        submitBtn.setOnClickListener {
            for (i in SearchSongAdapter.listSongSelected) {
                lifecycleScope.launch(Dispatchers.IO) {
                    userCase.addMusic.invoke(i)
                }
            }
            dialog.dismiss()

            val from = args.from

            if(from == 1) findNavController().popBackStack()
            else{
                val action = SelectionFragmentDirections.actionSelectionFragmentToPlaylistFragment()
                findNavController().navigate(action)
            }

            Toast.makeText(
                context,
                "Added Songs To The Playlist Successfully",
                Toast.LENGTH_SHORT
            ).show()
            SearchSongAdapter.listSongSelected = ArrayList()

        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.setCanceledOnTouchOutside(false)
    }


}


