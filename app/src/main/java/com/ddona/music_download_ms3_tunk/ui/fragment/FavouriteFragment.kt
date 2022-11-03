package com.ddona.music_download_ms3_tunk.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.ddona.music_download_ms3_tunk.adapter.FavouriteAdapter
import com.ddona.music_download_ms3_tunk.databinding.FragmentFavouriteBinding
import com.ddona.music_download_ms3_tunk.model.Data


class FavouriteFragment: Fragment(){
    private lateinit var binding:FragmentFavouriteBinding
    private lateinit var adapter: FavouriteAdapter

    val args: FavouriteFragmentArgs by navArgs()

    var status:Int = -1

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

        status = args.status

        val favouriteStatusList: ArrayList<Data> = ArrayList()

        for(i in favouriteList){
            if(i.status == status){
                favouriteStatusList.add(i)
            }
        }

        binding.favouriteRV.setHasFixedSize(true)
        adapter = FavouriteAdapter(requireContext(),favouriteStatusList)
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

