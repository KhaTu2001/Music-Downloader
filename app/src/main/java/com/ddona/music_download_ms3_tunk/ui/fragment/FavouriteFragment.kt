package com.example.newsapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ddona.music_download_ms3_tunk.adapter.FavouriteAdapter
import com.ddona.music_download_ms3_tunk.databinding.FragmentFavouriteBinding
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.viewmodel.SongViewModel


class FavouriteFragment: Fragment(){
    private lateinit var binding:FragmentFavouriteBinding
    private lateinit var adapter: FavouriteAdapter


    companion object{
        var favouriteList: ArrayList<Data> = ArrayList()
        var favouritesChanged: Boolean = false

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(inflater)
//        favouriteList = checkPlaylist(favouriteList)
        binding.favouriteRV.setHasFixedSize(true)
        adapter = FavouriteAdapter(requireContext(),favouriteList)
        binding.favouriteRV.adapter = adapter
        favouritesChanged = false

        return binding.root


    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        if(favouritesChanged) {
            adapter.updateFavourites(favouriteList)
            favouritesChanged = false
        }
    }


}