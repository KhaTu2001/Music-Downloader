package com.ddona.music_download_ms3_tunk.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.navigation.findNavController
import com.ddona.music_download_ms3_tunk.R
import com.ddona.music_download_ms3_tunk.api.SongApi
import com.ddona.music_download_ms3_tunk.callback.ConnectivityObserver
import com.ddona.music_download_ms3_tunk.db.MusicDAO
import com.ddona.music_download_ms3_tunk.model.*
import com.ddona.music_download_ms3_tunk.ui.fragment.ChangeRegionFragment
import com.ddona.music_download_ms3_tunk.ui.fragment.HomeFragmentDirections
import com.ddona.music_download_ms3_tunk.utils.NetworkConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    private val musicDAO: MusicDAO,
    private val songApi: SongApi, application: Application
) : AndroidViewModel(application) {

    var topListened = MutableLiveData<List<Data>>()
    var topTrending = MutableLiveData<List<Data>>()
    var topDownload = MutableLiveData<List<Data>>()
    var listGenre = MutableLiveData<List<DataX>>()
    var listSongByGenre = MutableLiveData<List<Data>>()
    var searchList = MutableLiveData<List<Data>>()
    var playlistmusicList = MutableLiveData<List<playlistMusic>>()

    private var connectivityObserver: ConnectivityObserver

    val isConnected = MutableLiveData(true)

    init {

        connectivityObserver = NetworkConnectivityObserver(application.applicationContext)
        connectivityObserver.observe().onEach {
            if (it == ConnectivityObserver.Status.Available){
                viewModelScope.launch(Dispatchers.IO) {
                        getAllTopListend()
                        getAllGenres()
                        getAllTopDownLoad()
                        getAllTopTrending()
                        getAllPlaylistList()

                }

                isConnected.postValue(true)
            }
            else{
                isConnected.postValue(false)
            }




        }.launchIn(viewModelScope)


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

    suspend fun getAllSongByGenres(keysearch: String) {
        val response = songApi.getAllSongByGenres(keysearch, ChangeRegionFragment.id_country)
        listSongByGenre.postValue(response.data)
    }

    suspend fun searchSong(songName: String) {
        val response = songApi.getSearchMusic(songName)
        searchList.postValue(response.data)

    }

    fun getAllMusicByPlaylist(playlist_id: Int): Flow<List<Data>> = musicDAO.getSongBYPlaylist(playlist_id)


    fun getAllPlaylist(): Flow<List<playlistMusic>> = musicDAO.getAllPlaylist()


    suspend fun getAllPlaylistList(){
         musicDAO.getAllPlaylist().collect{
             playlistmusicList.postValue(it)

        }
    }


}







