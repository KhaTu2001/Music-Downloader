package com.ddona.music_download_ms3_tunk.fr

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ddona.music_download_ms3_tunk.adapter.TopListAdapter
import com.ddona.music_download_ms3_tunk.callback.ListenedSongItemClick
import com.ddona.music_download_ms3_tunk.databinding.FragmentSearchBinding
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.ui.activity.PlayerActivity
import com.ddona.music_download_ms3_tunk.viewmodel.SongViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchFragment : Fragment(), ListenedSongItemClick {
    val viewModel: SongViewModel by activityViewModels()

    companion object{
        var searchListMA:ArrayList<Data> = ArrayList()
    }

    private lateinit var binding: FragmentSearchBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater)

        val adapter = TopListAdapter(requireContext(), this)
        binding.rvSearch.adapter = adapter
        var job: Job? = null
        binding.edtSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.searchSong(editable.toString())
                    }
                }
                searchListMA.clear()
            }
        }

        viewModel.searchList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            searchListMA.addAll(it)
            Log.d("abdad", "onCreateView:$searchListMA ")
        }
        return binding.root
    }


    override fun ListOnClick(index: Int) {
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("index", index)
        intent.putExtra("from", "SearchFragment")
        startActivity(intent)
    }
}