package com.ddona.music_download_ms3_tunk.reponsitory

import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.model.playlistMusic
import com.ddona.music_download_ms3_tunk.model.songDownloaded
import kotlinx.coroutines.flow.Flow

interface MusicRepository {

    fun getAllMusicByPlaylist(playlist_id: Int): Flow<List<Data>>

    fun getAllPlaylist(status:Int): Flow<List<playlistMusic>>

    fun getAllPlaylistByName(playlistName:String,status:Int): Flow<List<playlistMusic>>

    suspend fun countSong(playlist_id: Int): Flow<Int>

    fun checkName(playlist_name: String): Flow<Int>

    fun checkID(id: String): Flow<Int>

    suspend fun addMusicToDownloadlist(songDownloaded: songDownloaded)


    fun checkSongID(playlistID : Int,id: String): Flow<Int>


    suspend fun addMusicToPlaylist(data: Data)

    suspend fun addPlaylist(playlistMusic: playlistMusic)

     fun updatePlayListName(playlistName:String,id:Int)

     fun deletePlaylist(id:Int)

     fun deleteMusicSongFromPlaylist(id:String,playlistId: Int)

     fun deleteMusicSong(playlistId: Int)

    fun deleteMusicDownload(playlistId: String)






}