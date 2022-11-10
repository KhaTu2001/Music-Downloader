package com.ddona.music_download_ms3_tunk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ddona.music_download_ms3_tunk.adapter.DownloadPagerAdapter
import com.ddona.music_download_ms3_tunk.databinding.FragmentDownloadBinding


import com.google.android.material.tabs.TabLayoutMediator


class DownloadFragment : Fragment() {

    private lateinit var binding: FragmentDownloadBinding
    companion object{
        var index:Int = 0
        var tabs = arrayOf("Downloaded", "Downloading")

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDownloadBinding.inflate(inflater)
        val adapter = DownloadPagerAdapter(this, index)
        binding.vpDownload.adapter = adapter

        TabLayoutMediator(binding.tlDownload, binding.vpDownload) { tab, index ->
            tab.text = tabs[index]
        }.attach()




        return binding.root
    }



}