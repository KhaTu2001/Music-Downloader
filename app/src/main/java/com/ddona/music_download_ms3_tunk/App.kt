package com.ddona.music_download_ms3_tunk

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
//import com.tonyodev.fetch2.Fetch.Impl.setDefaultInstanceConfiguration
//import com.tonyodev.fetch2.FetchConfiguration
//import com.tonyodev.fetch2.HttpUrlConnectionDownloader
//import com.tonyodev.fetch2core.Downloader
//import com.tonyodev.fetch2okhttp.OkHttpDownloader
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient

@HiltAndroidApp
class App:Application() {
    companion object{
        const val CHANNEL_ID = "channel1"
        const val PLAY = "play"
        const val NEXT = "next"
        const val PREVIOUS = "previous"
        const val EXIT = "exit"
    }
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(CHANNEL_ID, "Now Playing Song", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description = "This is a important channel for showing song!!"

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)

//            if (BuildConfig.DEBUG) {
//                Timber.plant(DebugTree())
//            }
//            val  fetchConfiguration:FetchConfiguration = Builder(this)
//                .enableRetryOnNetworkGain(true)
//                .setDownloadConcurrentLimit(3)
//                .setHttpDownloader(HttpUrlConnectionDownloader(Downloader.FileDownloaderType.PARALLEL)) // OR
//                //.setHttpDownloader(getOkHttpDownloader())
//                .build()
//            setDefaultInstanceConfiguration(fetchConfiguration)
//            RxFetch.Impl.setDefaultRxInstanceConfiguration(fetchConfiguration)
        }
    }
//    private fun getOkHttpDownloader(): OkHttpDownloader? {
//        val okHttpClient: OkHttpClient = Builder().build()
//        return OkHttpDownloader(okHttpClient, Downloader.FileDownloaderType.PARALLEL)
//    }
}