package com.ddona.music_download_ms3_tunk.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ddona.music_download_ms3_tunk.api.SongApi
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.model.DataX
import com.ddona.music_download_ms3_tunk.ui.fragment.ChangeRegionFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(private val application: Application , private val songApi: SongApi) : ViewModel() {

    var topListened = MutableLiveData<List<Data>>()
    var topTrending = MutableLiveData<List<Data>>()
    var topDownload = MutableLiveData<List<Data>>()
    var listGenre = MutableLiveData<List<DataX>>()
    var listSongByGenre = MutableLiveData<List<Data>>()
    var searchList = MutableLiveData<List<Data>>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getAllTopListend()
            getAllGenres()
            getAllTopDownLoad()
            getAllTopTrending()
        }
    }


    private suspend fun getAllGenres() {
        val response = songApi.getAllGenres().data
        listGenre.postValue(response)
    }

     suspend fun getAllTopDownLoad() {
        val response = songApi.getAllTopDownLoad(ChangeRegionFragment.id_country)
        topDownload.postValue(response.data)
    }

     suspend fun getAllTopTrending() {
        val response = songApi.getAllTopTrending(ChangeRegionFragment.id_country)
        topTrending.postValue(response.data)
    }

    private suspend fun getAllTopListend() {
        val response = songApi.getAllTopListend().data
        topListened.postValue(response)
    }

     suspend fun getAllSongByGenres(keysearch:String){
        val response = songApi.getAllSongByGenres(keysearch,ChangeRegionFragment.id_country)
        listSongByGenre.postValue(response.data)
    }

    suspend fun searchSong(songName:String){
        val response = songApi.getSearchMusic(songName)
        searchList.postValue(response.data)
    }



}

