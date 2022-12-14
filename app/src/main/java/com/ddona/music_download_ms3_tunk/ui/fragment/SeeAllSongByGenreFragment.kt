package com.ddona.music_download_ms3_tunk.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddona.music_download_ms3_tunk.adapter.TopListAdapter
import com.ddona.music_download_ms3_tunk.callback.ListenedSongItemClick
import com.ddona.music_download_ms3_tunk.databinding.FragmentSeeAllSongByGenreBinding
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.ui.activity.PlayerActivity
import com.ddona.music_download_ms3_tunk.user_case.UseCases
import com.ddona.music_download_ms3_tunk.viewmodel.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SeeAllSongByGenreFragment : Fragment(),ListenedSongItemClick {

    val viewModel: SongViewModel by activityViewModels()

    @Inject
    lateinit var usercase: UseCases
    companion object{
        var topListSongByGenre: ArrayList<Data> = ArrayList()
        private lateinit var binding: FragmentSeeAllSongByGenreBinding

    }

    val args: SeeAllSongByGenreFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        var keysearch = args.keysearch
        var nameGenre = args.nameGenre
        binding = FragmentSeeAllSongByGenreBinding.inflate(inflater)
        binding.topTextview.text = nameGenre
        val tladapter = TopListAdapter(requireContext(),this,usercase)
        binding.rvSeeAllSong.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.rvSeeAllSong.adapter = tladapter
        MainScope().launch {
            viewModel.getAllSongByGenres(keysearch)
        }

        viewModel.listSongByGenre.observe(viewLifecycleOwner){
            tladapter.submitList(it)
            topListSongByGenre.clear()
            topListSongByGenre.addAll(it)


            binding.shimmerViewContainer.stopShimmerAnimation()
            binding.shimmerViewContainer.visibility = View.GONE

            binding.rvSeeAllSong.visibility = View.VISIBLE


        }

        return binding.root
    }

    override fun ListOnClick(index: Int) {
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("index",index)
        intent.putExtra("listSong",topListSongByGenre)
        intent.putExtra("from","Genres")
        startActivity(intent)
    }



    override fun onResume() {
        super.onResume()
        binding.shimmerViewContainer.startShimmerAnimation()
        if (binding.shimmerViewContainer.visibility == View.GONE)
            binding.rvSeeAllSong.visibility = View.VISIBLE

    }


}