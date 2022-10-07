package com.ddona.music_download_ms3_tunk.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.ddona.music_download_ms3_tunk.adapter.*
import com.ddona.music_download_ms3_tunk.callback.GenreItemClick
import com.ddona.music_download_ms3_tunk.callback.ListenedSongItemClick
import com.ddona.music_download_ms3_tunk.databinding.FragmentSeeAllSongBinding
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.ui.activity.PlayerActivity
import com.ddona.music_download_ms3_tunk.viewmodel.SongViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class SeeAllSongFragment : Fragment(), ListenedSongItemClick, GenreItemClick {

    private lateinit var binding: FragmentSeeAllSongBinding
    val viewModel: SongViewModel by activityViewModels()
    var topListSong: ArrayList<Data> = ArrayList()
    var downloadListSong: ArrayList<Data> = ArrayList()
    var trendingListSong: ArrayList<Data> = ArrayList()
    companion object{
        var localListSong:  ArrayList<Data> = ArrayList()
    }


    val args: SeeAllSongFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSeeAllSongBinding.inflate(inflater)



        if(args.data == 0){
            topTrendingRV()

        }

        if(args.data == 1){
            topListenedRV()
        }

        if(args.data == 2){
            topDownRV()
        }

        if(args.data == 3){
            genreRV()
        }

        return binding.root
    }
    private fun topTrendingRV() {
        binding.topTextview.text = "Top Trending"
        val ttadapter = TopListAdapter(requireContext(),this)
        binding.rvSeeAllSong.setHasFixedSize(true)
        binding.rvSeeAllSong.adapter = ttadapter
        viewModel.topTrending.observe(viewLifecycleOwner) {
            ttadapter.submitList( it)
            trendingListSong.addAll(it)
        }
        localListSong = trendingListSong
    }

    private fun genreRV() {
        binding.topTextview.text = "Genres"
        val gradapter = SeeAllGenreAdapter(this)
        binding.rvSeeAllSong.setHasFixedSize(true)
        binding.rvSeeAllSong.layoutManager =
            GridLayoutManager(context, 2)
        binding.rvSeeAllSong.adapter = gradapter
        viewModel.listGenre.observe(viewLifecycleOwner) {
            gradapter.submitList(it)
        }
    }

    private fun topDownRV() {
        binding.topTextview.text = "Top Download"
        val tdadapter = TopListAdapter(requireContext(),this)
        binding.rvSeeAllSong.setHasFixedSize(true)
        binding.rvSeeAllSong.adapter = tdadapter
        viewModel.topDownload.observe(viewLifecycleOwner) {
            tdadapter.submitList( it)
            downloadListSong.addAll(it)

        }
        localListSong = downloadListSong
    }

    private fun topListenedRV() {
        binding.topTextview.text = "Top Listened"
        val tladapter = TopListAdapter(requireContext(),this)
        binding.rvSeeAllSong.setHasFixedSize(true)
        binding.rvSeeAllSong.adapter = tladapter
        viewModel.topListened.observe(viewLifecycleOwner) {
            tladapter.submitList(it)
            topListSong.addAll(it)
        }
        localListSong = topListSong
    }

    override fun GenreOnClick(keySearch: String, nameGenre: String) {
        val keysearch = keySearch
        val nameGenre = nameGenre
        val action = SeeAllSongFragmentDirections.actionSeeAllSongFragmentToSeeAllSongByGenreFragment(keysearch,nameGenre)
        Navigation.findNavController(binding.root).navigate(action)

    }



    override fun ListOnClick(index: Int) {
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("index",index)
        intent.putExtra("from","SeeAllSong")
        startActivity(intent)

    }





}