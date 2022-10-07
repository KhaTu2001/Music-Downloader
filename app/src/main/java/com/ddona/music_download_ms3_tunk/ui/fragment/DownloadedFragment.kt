package com.ddona.music_download_ms3_tunk.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ddona.music_download_ms3_tunk.adapter.MusicAdapter
import com.ddona.music_download_ms3_tunk.databinding.FragmentDownloadedBinding
import com.ddona.music_download_ms3_tunk.ui.activity.MainActivity

class DownloadedFragment : Fragment() {

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
        musicAdapter = MusicAdapter(requireContext(), MainActivity.MusicListMA)
        binding.RvAllMusic.adapter = musicAdapter

        return binding.root

    }


}