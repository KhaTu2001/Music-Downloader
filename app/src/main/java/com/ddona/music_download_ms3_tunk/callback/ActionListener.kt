package com.ddona.music_download_ms3_tunk.callback

interface ActionListener {
    fun onPauseDownload(id: Int)

    fun onResumeDownload(id: Int)

    fun onRemoveDownload(id: Int)

    fun onRetryDownload(id: Int)
}