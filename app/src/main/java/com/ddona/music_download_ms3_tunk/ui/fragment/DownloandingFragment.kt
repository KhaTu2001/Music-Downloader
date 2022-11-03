package com.ddona.music_download_ms3_tunk.ui.fragment

//import com.ddona.music_download_ms3_tunk.adapter.FileAdapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ddona.music_download_ms3_tunk.adapter.FileAdapter
import com.ddona.music_download_ms3_tunk.databinding.FragmentDownloandingBinding
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.user_case.UseCases
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class DownloandingFragment : Fragment() {

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

        adapter = FileAdapter(requireContext(), musicList,usercase)

        binding.RvAllMusic.adapter = adapter

//      adapter = FileAdapter(requireContext())

//        binding.RvAllMusic.adapter = adapter
        return binding.root
    }



}