package com.ddona.music_download_ms3_tunk.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ddona.music_download_ms3_tunk.R
import com.ddona.music_download_ms3_tunk.databinding.FragmentDownloadedBinding
import com.ddona.music_download_ms3_tunk.databinding.FragmentDownloandingBinding
import com.ddona.music_download_ms3_tunk.databinding.FragmentHomeBinding

class DownloandingFragment : Fragment() {

    private lateinit var binding: FragmentDownloandingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDownloandingBinding.inflate(inflater)
        return binding.root
    }


}