package com.ddona.music_download_ms3_tunk.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ddona.music_download_ms3_tunk.R
import com.ddona.music_download_ms3_tunk.callback.ListenedSongItemClick
import com.ddona.music_download_ms3_tunk.callback.MyDownloadListener
import com.ddona.music_download_ms3_tunk.databinding.ItemsDownloadingBinding
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.model.comparePaths
import com.ddona.music_download_ms3_tunk.model.formatFileSize
import com.ddona.music_download_ms3_tunk.model.songDownloaded
import com.ddona.music_download_ms3_tunk.ui.fragment.DownloandingFragment
import com.ddona.music_download_ms3_tunk.user_case.UseCases
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ixuea.android.downloader.DownloadService
import com.ixuea.android.downloader.DownloadService.downloadManager
import com.ixuea.android.downloader.domain.DownloadInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.lang.ref.SoftReference


class FileAdapter(
    private val context: Context,
    private var musicList: ArrayList<Data>,
    private val userCases: UseCases,
    private val callback:ListenedSongItemClick

    ) :
    RecyclerView.Adapter<FileAdapter.ViewHolder>() {

    private var downloadInfo: DownloadInfo? = null

    companion object{
        var i = 0
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.items_downloading, parent, false)
        return ViewHolder(
            ItemsDownloadingBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }


    class ViewHolder(binding: ItemsDownloadingBinding) : RecyclerView.ViewHolder(binding.root) {
        val progressBar = binding.progressDownload
        val title = binding.songNamed
        val article = binding.songAuthor
        val image = binding.songImg
        val progressTextView = binding.progressCurren
        val option = binding.songOption


    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        val music2 = musicList[position]
        downloadManager = DownloadService.getDownloadManager(context.applicationContext)
        downloadInfo = downloadManager.getDownloadById(musicList[position].audioDownload)




        if (downloadInfo != null) {

            downloadInfo!!.downloadListener = object :
                MyDownloadListener(SoftReference<Any?>(this)) {
                override fun onRefresh() {
                    if (userTag != null && userTag.get() != null) {
                        when (downloadInfo!!.status) {

                            DownloadInfo.STATUS_NONE -> {
                                holder.progressBar.progress = 0
                                holder.progressTextView.text = "0%"
                                downloadManager.resume(downloadInfo)

                            }
                            DownloadInfo.STATUS_PAUSED, DownloadInfo.STATUS_ERROR -> {


                                try {
                                    holder.progressBar.progress =
                                        (downloadInfo!!.progress * 100.0 / downloadInfo!!.size).toInt()

                                    holder.progressTextView.text =
                                        formatFileSize(downloadInfo!!.progress * 100.0 / downloadInfo!!.size) + "%"
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                                downloadManager.resume(downloadInfo)


                            }

                            DownloadInfo.STATUS_DOWNLOADING, DownloadInfo.STATUS_PREPARE_DOWNLOAD -> {

                                try {
                                    holder.progressBar.progress =
                                        (downloadInfo!!.progress * 100.0 / downloadInfo!!.size).toInt()

                                    holder.progressTextView.text =
                                        formatFileSize(downloadInfo!!.progress * 100.0 / downloadInfo!!.size) + "%"
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }



                            }

                            DownloadInfo.STATUS_COMPLETED -> {
                                val music = songDownloaded(
                                    id = music2.id,
                                    albumId = music2.albumId,
                                    albumName = music2.albumName,
                                    artistId = music2.artistId,
                                    audio = music2.audio,
                                    artistName = music2.artistName,
                                    duration = music2.duration,
                                    image = music2.image,
                                    name = music2.name,
                                    playlist_id = -1,
                                    status = 1
                                )


                                CoroutineScope(Dispatchers.Main).launch {
                                    userCases.addMusicToDownloadlistUserCase.invoke(music)
                                }

                                try {
                                    holder.progressBar.progress =
                                        (downloadInfo!!.progress * 100.0 / downloadInfo!!.size).toInt()

                                    holder.progressTextView.text =
                                        formatFileSize(downloadInfo!!.progress * 100.0 / downloadInfo!!.size) + "%"
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                callback.ListOnClick(position)

                                val file = File(Environment.getExternalStoragePublicDirectory("Music Downloader").path)
                                file.listFiles()?.forEach { musicFile ->
                                    if (musicFile.length() != 0L && !comparePaths(musicFile.path)) {

                                        MediaScannerConnection.scanFile(
                                            context,
                                            listOf(musicFile.path).toTypedArray(),
                                            null,
                                            null
                                        )
                                    }
                                }

                            }

                            DownloadInfo.STATUS_REMOVED ->{
                                holder.progressBar.progress = 0
                                holder.progressTextView.text = "0%"
                            }
                        }
                    }
                }
            }


        }
        else {

            val d = File(Environment.getExternalStoragePublicDirectory("Music Downloader"), "")
            if (!d.exists()) {
                d.mkdirs()
            }
            val path = d.absolutePath + "/" + musicList[position].name+".mp3"

            downloadInfo = DownloadInfo.Builder().setUrl(musicList[position].audioDownload)
                .setPath(path)
                .build()
            downloadInfo!!.downloadListener = object :
                MyDownloadListener(SoftReference<Any?>(this)) {
                override fun onRefresh() {
                    if (userTag != null && userTag.get() != null) {

                        holder.option.setOnClickListener {
                            showBottomSheetDialogAdapter(music2, position, downloadInfo!!)
                        }

                        when (downloadInfo!!.status) {

                            DownloadInfo.STATUS_NONE -> {
                                holder.progressBar.progress = 0
                                holder.progressTextView.text = "0%"
                                downloadManager.resume(downloadInfo)

                            }
                            DownloadInfo.STATUS_PAUSED, DownloadInfo.STATUS_ERROR -> {


                                try {
                                    holder.progressBar.progress =
                                        (downloadInfo!!.progress * 100.0 / downloadInfo!!.size).toInt()

                                    holder.progressTextView.text =
                                        formatFileSize(downloadInfo!!.progress * 100.0 / downloadInfo!!.size) + "%"
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                downloadManager.resume(downloadInfo)


                            }

                            DownloadInfo.STATUS_DOWNLOADING, DownloadInfo.STATUS_PREPARE_DOWNLOAD -> {


                                try {
                                    holder.progressBar.progress =
                                        (downloadInfo!!.progress * 100.0 / downloadInfo!!.size).toInt()

                                    holder.progressTextView.text =
                                        formatFileSize(downloadInfo!!.progress * 100.0 / downloadInfo!!.size) + "%"
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }



                            }

                            DownloadInfo.STATUS_COMPLETED -> {


                                val music = songDownloaded(
                                    id = music2.id,
                                    albumId = music2.albumId,
                                    albumName = music2.albumName,
                                    artistId = music2.artistId,
                                    audio = music2.audio,
                                    artistName = music2.artistName,
                                    duration = music2.duration,
                                    image = music2.image,
                                    name = music2.name,
                                    playlist_id = -1,
                                    status = 1
                                )

                                CoroutineScope(Dispatchers.Main).launch {
                                    userCases.addMusicToDownloadlistUserCase.invoke(music)
                                }

                                try {
                                    holder.progressBar.progress =
                                        (downloadInfo!!.progress * 100.0 / downloadInfo!!.size).toInt()

                                    holder.progressTextView.text =
                                        formatFileSize(downloadInfo!!.progress * 100.0 / downloadInfo!!.size) + "%"
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                                downloadManager.remove(downloadInfo)

                                callback.ListOnClick(position)

                                val file = File(Environment.getExternalStoragePublicDirectory("Music Downloader").path)
                                file.listFiles()?.forEach { musicFile ->
                                    if (musicFile.length() != 0L && !comparePaths(musicFile.path)) {

                                        MediaScannerConnection.scanFile(
                                            context,
                                            listOf(musicFile.path).toTypedArray(),
                                            null,
                                            null
                                        )
                                    }
                                }
                            }

                            DownloadInfo.STATUS_REMOVED ->{
                                holder.progressBar.progress = 0
                                holder.progressTextView.text = "0%"
                            }
                        }
                    }
                }
            }
            downloadManager.download(downloadInfo)


        }



        if (musicList.isNotEmpty()) {
            if (downloadInfo == null) {
                holder.progressBar.progress = 0
                holder.progressTextView.text = "0%"
            }
            else {
                holder.option.setOnClickListener {
                    showBottomSheetDialogAdapter(music2, position, downloadInfo!!)
                }

                when (downloadInfo!!.status) {

                    DownloadInfo.STATUS_NONE -> {
                        holder.progressBar.progress = 0
                        holder.progressTextView.text = "0%"
                        downloadManager.resume(downloadInfo)

                    }
                    DownloadInfo.STATUS_PAUSED, DownloadInfo.STATUS_ERROR -> {


                        try {
                            holder.progressBar.progress =
                                (downloadInfo!!.progress * 100.0 / downloadInfo!!.size).toInt()

                            holder.progressTextView.text =
                                formatFileSize(downloadInfo!!.progress * 100.0 / downloadInfo!!.size) + "%"
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        downloadManager.resume(downloadInfo)

                    }

                    DownloadInfo.STATUS_DOWNLOADING, DownloadInfo.STATUS_PREPARE_DOWNLOAD -> {

                        try {
                            holder.progressBar.progress =
                                (downloadInfo!!.progress * 100.0 / downloadInfo!!.size).toInt()

                            holder.progressTextView.text =
                                formatFileSize(downloadInfo!!.progress * 100.0 / downloadInfo!!.size) + "%"
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }



                    }

                    DownloadInfo.STATUS_COMPLETED -> {
                        val music = songDownloaded(
                            id = music2.id,
                            albumId = music2.albumId,
                            albumName = music2.albumName,
                            artistId = music2.artistId,
                            audio = music2.audio,
                            artistName = music2.artistName,
                            duration = music2.duration,
                            image = music2.image,
                            name = music2.name,
                            playlist_id = -1,
                            status = 1
                        )


                        CoroutineScope(Dispatchers.Main).launch {
                            userCases.addMusicToDownloadlistUserCase.invoke(music)
                        }

                        try {
                            holder.progressBar.progress =
                                (downloadInfo!!.progress * 100.0 / downloadInfo!!.size).toInt()

                            holder.progressTextView.text =
                                formatFileSize(downloadInfo!!.progress * 100.0 / downloadInfo!!.size) + "%"
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


                    }

                    DownloadInfo.STATUS_REMOVED ->{
                        holder.progressBar.progress = 0
                        holder.progressTextView.text = "0%"
                    }
                }
            }

            holder.title.text = musicList[position].name
            holder.article.text = musicList[position].artistName

            Glide.with(context)
                .load(musicList[position].image)
                .apply(
                    RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen)
                        .centerCrop()
                )
                .into(holder.image)


        }

        Log.d("SAdds", "onBindViewHolder: ${downloadManager.findAllDownloading()}")
        Log.d("SAdds", "onBindViewHolder: ${downloadManager.findAllDownloaded()}")


    }


    override fun getItemCount(): Int {
        return musicList.size
    }


    fun showBottomSheetDialogAdapter(data: Data, position: Int,downloadInfo: DownloadInfo) {

        val bottomSheet = BottomSheetDialog(context)
        bottomSheet.setContentView(R.layout.bottomsheet_download_option)

        val pauseDownload = bottomSheet.findViewById<View>(R.id.ln_pause_download)
        val downloading = bottomSheet.findViewById<View>(R.id.ln_play_download)
        val removeDownload = bottomSheet.findViewById<View>(R.id.ln_remove_downloading)
        val addShareSong = bottomSheet.findViewById<View>(R.id.ln_share_song)


        when (downloadInfo.status) {

            DownloadInfo.STATUS_PAUSED -> {
                downloading!!.visibility = View.VISIBLE
                pauseDownload!!.visibility = View.GONE

            }

            DownloadInfo.STATUS_DOWNLOADING -> {
                downloading!!.visibility = View.GONE
                pauseDownload!!.visibility = View.VISIBLE

            }
        }

        pauseDownload?.setOnClickListener {
            downloadManager.pause(downloadInfo)
            bottomSheet.dismiss()
        }

        downloading?.setOnClickListener {
            downloadManager.resume(downloadInfo)
            bottomSheet.dismiss()
        }

        removeDownload?.setOnClickListener {
            downloadManager.remove(downloadInfo)
            musicList.removeAt(position)
            DownloandingFragment.musicList = musicList
            notifyDataSetChanged()

            bottomSheet.dismiss()
        }

        addShareSong?.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, data.audio)
            ContextCompat.startActivity(
                context,
                Intent.createChooser(shareIntent, "Sharing Music File!!"),
                null
            )

            bottomSheet.dismiss()
        }

        bottomSheet.show()

    }
}