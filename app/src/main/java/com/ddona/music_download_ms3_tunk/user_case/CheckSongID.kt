package com.ddona.music_download_ms3_tunk.user_case

import com.ddona.music_download_ms3_tunk.reponsitory.MusicRepository
import kotlinx.coroutines.flow.Flow

class CheckSongID(private val repo: MusicRepository) {

    operator fun invoke(playlistID: Int, id: String): Flow<Int>
    {
        return repo.checkSongID(playlistID,id)
    }

}