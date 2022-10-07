package com.ddona.music_download_ms3_tunk.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ddona.music_download_ms3_tunk.databinding.FragmentOnlineMusicBinding
import com.ddona.music_download_ms3_tunk.databinding.FragmentPlaylistBinding

class OnlineMusicFragment : Fragment() {

    private lateinit var binding: FragmentOnlineMusicBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOnlineMusicBinding.inflate(inflater)
        return binding.root
    }


}