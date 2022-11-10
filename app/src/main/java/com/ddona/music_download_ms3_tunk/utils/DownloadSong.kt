package com.ddona.music_download_ms3_tunk.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.ddona.music_download_ms3_tunk.ui.activity.MainActivity
import com.ixuea.android.downloader.domain.DownloadInfo


private var downloadInfo: DownloadInfo? = null

fun startDownloadSong(url:String,context: Context){


    if(MainActivity.permssion == 1)
    try {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val linkUrl = Uri.parse(url)
        val request = DownloadManager.Request(linkUrl)
        request
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
            .setMimeType("application/mp3")
            .setAllowedOverRoaming(false)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle("Downloading")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC,
                System.currentTimeMillis().toString()
            )
        downloadManager.enqueue(request)
        Toast.makeText(context,"Song is Downloaded",Toast.LENGTH_LONG).show()
    }
    catch (e:Exception){
        Toast.makeText(context,"Song Download Failed",Toast.LENGTH_LONG).show()

    }

}


