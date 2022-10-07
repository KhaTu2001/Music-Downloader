package com.ddona.music_download_ms3_tunk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ddona.music_download_ms3_tunk.adapter.PlaylistPagerAdapter
import com.ddona.music_download_ms3_tunk.databinding.FragmentPlaylistBinding

import com.google.android.material.tabs.TabLayoutMediator


class PlaylistFragment : Fragment() {

    private lateinit var binding: FragmentPlaylistBinding
    private val tabs = arrayOf("MyMusic", "OnlineMusic")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistBinding.inflate(inflater)
        val adapter = PlaylistPagerAdapter(this)
        binding.vpPlaylist.adapter = adapter

        TabLayoutMediator(binding.tlPlaylist, binding.vpPlaylist) { tab, index ->
            tab.text = tabs[index]
        }.attach()


        return binding.root
    }


}