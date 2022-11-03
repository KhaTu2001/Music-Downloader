package com.ddona.music_download_ms3_tunk.user_case

import com.ddona.music_download_ms3_tunk.db.MusicDAO
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.model.playlistMusic
import com.ddona.music_download_ms3_tunk.model.songDownloaded
import com.ddona.music_download_ms3_tunk.reponsitory.MusicRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val musicDAO: MusicDAO
):MusicRepository {
    override fun getAllMusicByPlaylist(playlist_id: Int): Flow<List<Data>> {
        return musicDAO.getSongBYPlaylist(playlist_id)
    }

    override fun getAllPlaylist(status:Int): Flow<List<playlistMusic>> {
        return musicDAO.getAllPlaylist(status)
    }

    override fun getAllPlaylistByName(playlistName: String,status: Int): Flow<List<playlistMusic>> {
        return musicDAO.getAllPlaylistByName(playlistName,status)

    }

    override suspend fun countSong(playlist_id: Int): Flow<Int> {
        return musicDAO.getCount(playlist_id)
    }

    override fun checkName(playlist_name: String): Flow<Int> {
        return musicDAO.checkName(playlist_name)
    }

    override fun checkID(id: String): Flow<Int> {
        return musicDAO.checkid(id)
    }

    override suspend fun addMusicToDownloadlist(songDownloaded: songDownloaded) {
        musicDAO.addSongtoDownloadlist(songDownloaded)
    }

    override fun checkSongID(playlistID: Int, id: String): Flow<Int> {
        return musicDAO.checkSongName(playlistID,id)
    }

    override suspend fun addMusicToPlaylist(data: Data) {
        musicDAO.addSongtoPlaylist(data)
    }

    override suspend fun addPlaylist(playlistMusic: playlistMusic) {
        musicDAO.addPlaylist(playlistMusic)
    }

    override fun updatePlayListName(playlistName: String, id: Int) {
        musicDAO.updatePlayListName(playlistName,id)
    }

    override fun deletePlaylist(id: Int) {
        musicDAO.deletePlaylist(id)
    }

    override fun deleteMusicSongFromPlaylist(id: String, playlistId: Int) {
        musicDAO.deleteMusicSongFromPlaylist(id,playlistId)
    }

    override fun deleteMusicSong(playlistId: Int) {
        musicDAO.deleteMusicSong(playlistId)
    }

    override  fun deleteMusicDownload(id: String) {
        musicDAO.deleteMusicDonwloaded(id)
    }

}