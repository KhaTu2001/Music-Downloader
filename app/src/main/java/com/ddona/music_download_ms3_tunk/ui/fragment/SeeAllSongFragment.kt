package com.ddona.music_download_ms3_tunk.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.marginLeft
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
import com.ddona.music_download_ms3_tunk.user_case.UseCases
import com.ddona.music_download_ms3_tunk.viewmodel.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint

class SeeAllSongFragment : Fragment(), ListenedSongItemClick, GenreItemClick {

    private lateinit var binding: FragmentSeeAllSongBinding

    val viewModel: SongViewModel by activityViewModels()
    var topListSong: ArrayList<Data> = ArrayList()
    var downloadListSong: ArrayList<Data> = ArrayList()
    var trendingListSong: ArrayList<Data> = ArrayList()

    companion object{
        var localListSong:  ArrayList<Data> = ArrayList()
    }

    @Inject
    lateinit var usercase: UseCases

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
        val ttadapter = TopListAdapter(requireContext(),this,usercase)
        binding.rvSeeAllSong.setHasFixedSize(true)
        binding.rvSeeAllSong.adapter = ttadapter
        viewModel.topTrending.observe(viewLifecycleOwner) {
            ttadapter.submitList( it)
            trendingListSong.addAll(it)


            binding.shimmerViewContainer.stopShimmerAnimation()
            binding.shimmerViewContainer.visibility = View.GONE
            binding.rvSeeAllSong.visibility = View.VISIBLE
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

            binding.rvSeeAllSong.marginLeft
            binding.shimmerViewContainer.stopShimmerAnimation()
            binding.shimmerViewContainer.visibility = View.GONE
            binding.rvSeeAllSong.visibility = View.VISIBLE
        }
    }

    private fun topDownRV() {
        binding.topTextview.text = "Top Download"
        val tdadapter = TopListAdapter(requireContext(),this,usercase)
        binding.rvSeeAllSong.setHasFixedSize(true)
        binding.rvSeeAllSong.adapter = tdadapter
        viewModel.topDownload.observe(viewLifecycleOwner) {
            tdadapter.submitList( it)
            downloadListSong.addAll(it)
            binding.shimmerViewContainer.stopShimmerAnimation()
            binding.shimmerViewContainer.visibility = View.GONE

            binding.rvSeeAllSong.visibility = View.VISIBLE
        }
        localListSong = downloadListSong
    }

    private fun topListenedRV() {
        binding.topTextview.text = "Top Listened"
        val tladapter = TopListAdapter(requireContext(),this,usercase)
        binding.rvSeeAllSong.setHasFixedSize(true)
        binding.rvSeeAllSong.adapter = tladapter
        viewModel.topListened.observe(viewLifecycleOwner) {
            tladapter.submitList(it)
            topListSong.addAll(it)
            binding.shimmerViewContainer.stopShimmerAnimation()
            binding.shimmerViewContainer.visibility = View.GONE

            binding.rvSeeAllSong.visibility = View.VISIBLE
        }
        localListSong = topListSong
    }

    override fun GenreOnClick(keySearch: String, nameGenre: String) {
        val keysearch = keySearch
        val nameGenre = nameGenre
        val action = SeeAllSongFragmentDirections.actionSeeAllSongFragmentToSeeAllSongByGenreFragment(
            keysearch,
            nameGenre
        )
        Navigation.findNavController(binding.root).navigate(action)
        Log.d("dfgdf", "GenreOnClick:$keysearch , $nameGenre  ")
    }



    override fun ListOnClick(index: Int) {
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("index",index)
        intent.putExtra("from","SeeAllSong")
        startActivity(intent)

    }

    override fun onResume() {
        super.onResume()
        binding.shimmerViewContainer.startShimmerAnimation()

        if (binding.shimmerViewContainer.visibility == View.GONE){
            binding.rvSeeAllSong.visibility = View.VISIBLE
        }



    }




}