package com.ddona.music_download_ms3_tunk.callback

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    fun observe(): Flow<Status>

    enum class Status{
        Available,Unavailable,Losing,Lost
    }
}