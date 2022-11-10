package com.ddona.music_download_ms3_tunk.ui.fragment

//import com.ddona.music_download_ms3_tunk.adapter.FileAdapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.ddona.music_download_ms3_tunk.R
import com.ddona.music_download_ms3_tunk.adapter.FileAdapter
import com.ddona.music_download_ms3_tunk.callback.ListenedSongItemClick
import com.ddona.music_download_ms3_tunk.databinding.FragmentDownloandingBinding
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.user_case.UseCases
import com.ixuea.android.downloader.DownloadService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class DownloandingFragment : Fragment(),ListenedSongItemClick {

    private lateinit var binding: FragmentDownloandingBinding
    private lateinit var adapter: FileAdapter

    @Inject
    lateinit var usercase: UseCases

    companion object{
         var musicList: ArrayList<Data> = ArrayList()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDownloandingBinding.inflate(inflater)
        Log.d("asdsa", "1: $musicList")

        adapter = FileAdapter(requireContext(), musicList,usercase,this)

        binding.RvAllMusic.adapter = adapter


        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()


        DownloadFragment.index = 0
        DownloadFragment.tabs = arrayOf("Downloaded", "Downloading")
    }

    override fun ListOnClick(index: Int) {
        Log.d("SAdds", "ListOnClick: ${DownloadService.downloadManager.findAllDownloading()}")

        if(DownloadService.downloadManager.findAllDownloading().isEmpty()){

            musicList = ArrayList()
            adapter = FileAdapter(requireContext(), ArrayList(),usercase,this)
            binding.RvAllMusic.adapter = adapter

        }

    }

}