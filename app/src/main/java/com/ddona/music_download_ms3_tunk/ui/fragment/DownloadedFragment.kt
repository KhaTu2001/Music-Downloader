package com.ddona.music_download_ms3_tunk.ui.fragment

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.room.util.FileUtil
import com.ddona.music_download_ms3_tunk.adapter.MusicAdapter
import com.ddona.music_download_ms3_tunk.databinding.FragmentDownloadedBinding
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.ui.activity.MainActivity
import com.ddona.music_download_ms3_tunk.user_case.UseCases
import com.ixuea.android.downloader.DownloadService.downloadManager
import com.ixuea.android.downloader.domain.DownloadInfo
import com.ixuea.android.downloader.exception.DownloadException
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class DownloadedFragment : Fragment() {

    @Inject
    lateinit var useCases: UseCases

    private lateinit var binding: FragmentDownloadedBinding
    companion object{
         lateinit var musicAdapter: MusicAdapter
        lateinit var MusicListMA: ArrayList<Data>

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDownloadedBinding.inflate(inflater)

        binding.RvAllMusic.setHasFixedSize(true)
        MusicListMA = MainActivity.MusicListMA
        musicAdapter = MusicAdapter(requireContext(), MusicListMA,useCases)

        binding.RvAllMusic.adapter = musicAdapter


        return binding.root

        //set download callback.

    }



}