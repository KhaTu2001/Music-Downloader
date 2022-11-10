package com.ddona.music_download_ms3_tunk.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ddona.music_download_ms3_tunk.databinding.FragmentPlaylistDetailsTemporaryBinding
import com.ddona.music_download_ms3_tunk.model.playlistMusic
import com.ddona.music_download_ms3_tunk.ui.activity.MainActivity
import com.ddona.music_download_ms3_tunk.viewmodel.SongViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlaylistDetailsTemporaryFragment : Fragment() {

    private lateinit var binding: FragmentPlaylistDetailsTemporaryBinding

    private var currentPlaylistPos: Int = -1
    var playlistName: String = ""
    var status: Int = -1


    val viewModel: SongViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistDetailsTemporaryBinding.inflate(layoutInflater)

        viewModel.playlistmusicList.observe(viewLifecycleOwner) {
//            Log.d("DAsd", "onCreateView: $it")
            if(it.isEmpty()){
                currentPlaylistPos = 0
                playlistName=""
            }
            else{
                currentPlaylistPos =  it[it.size - 1].playList_ID!!
                playlistName =  it[it.size - 1].playlistName
                status = it[it.size - 1].status

            }

            binding.playlistName.text = playlistName
            binding.btnAddSong.setOnClickListener {
                val action =
                    PlaylistDetailsTemporaryFragmentDirections.actionPlaylistDetailsTemporaryFragmentToSelectionFragment(
                        currentPlaylistPos,
                        2,
                        status
                    )
                findNavController().navigate(action)
            }

        }

        return binding.root
    }




}