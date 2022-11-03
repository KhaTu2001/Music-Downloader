package com.ddona.music_download_ms3_tunk.user_case

import com.ddona.music_download_ms3_tunk.reponsitory.MusicRepository
import kotlinx.coroutines.flow.Flow

class CheckID(private val repo: MusicRepository) {

    operator fun invoke(id:String): Flow<Int>
    {
        return repo.checkID(id)
    }

}