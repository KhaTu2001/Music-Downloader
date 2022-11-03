package com.ddona.music_download_ms3_tunk.callback

import com.ixuea.android.downloader.callback.AbsDownloadListener
import com.ixuea.android.downloader.exception.DownloadException
import java.lang.ref.SoftReference

abstract class MyDownloadListener : AbsDownloadListener {
    constructor() : super() {}
    constructor(userTag: SoftReference<Any?>?) : super(userTag) {}

    override fun onStart() {
        onRefresh()
    }

    abstract fun onRefresh()

    override fun onWaited() {
        onRefresh()
    }

    override fun onDownloading(progress: Long, size: Long) {
        onRefresh()
    }

    override fun onRemoved() {
        onRefresh()
    }

    override fun onDownloadSuccess() {
        onRefresh()
    }

    override fun onDownloadFailed(e: DownloadException) {
        onRefresh()
    }

    override fun onPaused() {
        onRefresh()
    }
}