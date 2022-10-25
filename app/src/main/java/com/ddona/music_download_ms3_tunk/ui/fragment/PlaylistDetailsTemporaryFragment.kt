package com.ddona.music_download_ms3_tunk.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ddona.music_download_ms3_tunk.databinding.FragmentPlaylistDetailsTemporaryBinding
import com.ddona.music_download_ms3_tunk.ui.activity.MainActivity
import com.ddona.music_download_ms3_tunk.viewmodel.SongViewModel

class PlaylistDetailsTemporaryFragment : Fragment() {

    private lateinit var binding: FragmentPlaylistDetailsTemporaryBinding
//    private val args: PlaylistDetailsFragmentArgs by navArgs()

    private var currentPlaylistPos: Int = -1
    var playlistName: String = ""

    val viewModel: SongViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistDetailsTemporaryBinding.inflate(layoutInflater)

        val size:Int = MainActivity.playlistList.size - 1
        currentPlaylistPos = MainActivity.playlistList[size].playList_ID!!
        playlistName = MainActivity.playlistList[size].playlistName


        binding.playlistName.text = playlistName

        binding.btnAddSong.setOnClickListener {
            val action =
                PlaylistDetailsTemporaryFragmentDirections.actionPlaylistDetailsTemporaryFragmentToSelectionFragment(
                    currentPlaylistPos,
                    2
                )
            findNavController().navigate(action)
        }

        return binding.root
    }


}