package com.ddona.music_download_ms3_tunk.user_case

import com.ddona.music_download_ms3_tunk.db.MusicDAO
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.model.playlistMusic
import com.ddona.music_download_ms3_tunk.reponsitory.MusicRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val musicDAO: MusicDAO
):MusicRepository {
    override fun getAllMusicByPlaylist(playlist_id: Int): Flow<List<Data>> {
        return musicDAO.getSongBYPlaylist(playlist_id)
    }

    override fun getAllPlaylist(): Flow<List<playlistMusic>> {
        return musicDAO.getAllPlaylist()
    }

    override fun getAllPlaylistByName(playlistName: String): Flow<List<playlistMusic>> {
        return musicDAO.getAllPlaylistByName(playlistName)

    }

    override suspend fun countSong(playlist_id: Int): Flow<Int> {
        return musicDAO.getCount(playlist_id)
    }

    override suspend fun checkName(playlist_name: String): Flow<Int> {
        return musicDAO.checkName(playlist_name)
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

}