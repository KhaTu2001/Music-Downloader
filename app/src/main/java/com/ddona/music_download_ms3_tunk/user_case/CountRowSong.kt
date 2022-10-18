package com.ddona.music_download_ms3_tunk.user_case

import com.ddona.music_download_ms3_tunk.reponsitory.MusicRepository
import kotlinx.coroutines.flow.Flow

class CountRowSong(private val repo: MusicRepository) {

    suspend operator fun invoke(playlist_id:Int): Flow<Int>
    {
        return repo.countSong(playlist_id)
    }

}