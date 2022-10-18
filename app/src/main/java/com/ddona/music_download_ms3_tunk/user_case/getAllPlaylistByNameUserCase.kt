package com.ddona.music_download_ms3_tunk.user_case

import com.ddona.music_download_ms3_tunk.model.playlistMusic
import com.ddona.music_download_ms3_tunk.reponsitory.MusicRepository
import kotlinx.coroutines.flow.Flow

class getAllPlaylistByNameUserCase(private val repo: MusicRepository) {

    operator fun invoke(playlistName:String): Flow<List<playlistMusic>>
    {
        return repo.getAllPlaylistByName(playlistName)
    }

}