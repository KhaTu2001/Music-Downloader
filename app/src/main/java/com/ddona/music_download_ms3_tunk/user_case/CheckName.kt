package com.ddona.music_download_ms3_tunk.user_case

import com.ddona.music_download_ms3_tunk.reponsitory.MusicRepository
import kotlinx.coroutines.flow.Flow

class CheckName(private val repo: MusicRepository) {

    suspend operator fun invoke(playlist_name:String): Flow<Int>
    {
        return repo.checkName(playlist_name)
    }

}