package com.ddona.music_download_ms3_tunk.user_case

import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.model.playlistMusic
import com.ddona.music_download_ms3_tunk.reponsitory.MusicRepository
import kotlinx.coroutines.flow.Flow

class getAllMusicByPlaylistUserCase(private val repo: MusicRepository) {

    operator fun invoke(playListID:Int): Flow<List<Data>>
    {
        return repo.getAllMusicByPlaylist(playListID)
    }

}