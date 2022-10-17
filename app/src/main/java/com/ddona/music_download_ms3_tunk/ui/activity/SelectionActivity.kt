package com.ddona.music_download_ms3_tunk.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.ddona.music_download_ms3_tunk.adapter.MusicAdapter
import com.ddona.music_download_ms3_tunk.databinding.ActivitySelectionBinding
import com.ddona.music_download_ms3_tunk.user_case.UseCases
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectionBinding
    private lateinit var adapter: MusicAdapter

    @Inject
    lateinit var useCases: UseCases

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectionBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.selectionRV.setItemViewCacheSize(30)
        binding.selectionRV.setHasFixedSize(true)
        binding.selectionRV.layoutManager = LinearLayoutManager(this)
        adapter = MusicAdapter(this, MainActivity.MusicListMA, selectionActivity = true,useCases)
        binding.selectionRV.adapter = adapter
        binding.backBtnSA.setOnClickListener { finish() }
        binding.searchViewSA.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                MainActivity.musicListSearch = ArrayList()
                if(newText != null){
                    val userInput = newText.lowercase()
                    for (song in MainActivity.MusicListMA)
                        if(song.name.lowercase().contains(userInput))
                            MainActivity.musicListSearch.add(song)
                    MainActivity.search = true
//                    adapter.updateMusicList(searchList = MainActivity.musicListSearch)
                }
                return true

            }
        })
    }



}