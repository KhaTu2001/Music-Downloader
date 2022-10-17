package com.ddona.music_download_ms3_tunk.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ddona.music_download_ms3_tunk.adapter.MusicAdapter
import com.ddona.music_download_ms3_tunk.databinding.FragmentDownloadedBinding
import com.ddona.music_download_ms3_tunk.ui.activity.MainActivity
import com.ddona.music_download_ms3_tunk.user_case.UseCases
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DownloadedFragment : Fragment() {

    @Inject
    lateinit var useCases: UseCases

    private lateinit var binding: FragmentDownloadedBinding
    companion object{
         lateinit var musicAdapter: MusicAdapter

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDownloadedBinding.inflate(inflater)

        binding.RvAllMusic.setHasFixedSize(true)
        musicAdapter = MusicAdapter(requireContext(), MainActivity.MusicListMA,false,useCases,true)
        binding.RvAllMusic.adapter = musicAdapter

        return binding.root

    }


}