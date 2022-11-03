package com.ddona.music_download_ms3_tunk.adapter

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ddona.music_download_ms3_tunk.R
import com.ddona.music_download_ms3_tunk.callback.MyDownloadListener
import com.ddona.music_download_ms3_tunk.databinding.ItemsDownloadingBinding
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.model.formatDuration
import com.ddona.music_download_ms3_tunk.model.formatFileSize
import com.ddona.music_download_ms3_tunk.model.songDownloaded
import com.ddona.music_download_ms3_tunk.ui.activity.PlayerActivity
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
import java.sql.SQLException


class FileAdapter(
    private val context: Context,
    private val musicList: ArrayList<Data>,
    private val userCases: UseCases


) :
    RecyclerView.Adapter<FileAdapter.ViewHolder>() {

    private var downloadInfo: DownloadInfo? = null
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val music2 = musicList[position]
        downloadManager = DownloadService.getDownloadManager(context.applicationContext)
        downloadInfo = downloadManager.getDownloadById(musicList[position].audioDownload)
        if (downloadInfo != null) {

//            downloadInfo!!.downloadListener = object :
//                MyDownloadListener(SoftReference<Any?>(this)) {
//                override fun onRefresh() {
//                    if (userTag != null && userTag.get() != null) {
//                        val viewHolder: ViewHolder = userTag.get() as ViewHolder
                        when (downloadInfo!!.status) {

                            DownloadInfo.STATUS_NONE -> {
                                holder.progressBar.progress = 0
                                holder.progressTextView.text = "0%"
                                downloadManager.resume(downloadInfo)

                            }
                            DownloadInfo.STATUS_PAUSED , DownloadInfo.STATUS_ERROR -> {


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

                                handler = Handler(Looper.myLooper()!!)
                                runnable = Runnable {
                                    holder.progressBar.progress =
                                        (downloadInfo!!.progress * 100.0 / downloadInfo!!.size).toInt()

                                    holder.progressTextView.text =
                                        formatFileSize(downloadInfo!!.progress * 100.0 / downloadInfo!!.size) + "%"
                                }
                            handler.postDelayed(runnable,200)


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
                                downloadInfo = null

                                DownloandingFragment.musicList = musicList



                            }
                        }
//                    }
//                }
//            }


        } else {
            holder.progressBar.progress = 0
            holder.progressTextView.text = "0%"

            val d = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "d")
            if (!d.exists()) {
                d.mkdirs()
            }
            val path = d.absolutePath + "/" + musicList[position].name
            downloadInfo = DownloadInfo.Builder().setUrl(musicList[position].audioDownload)
                .setPath(path)
                .build()
//            downloadInfo!!.downloadListener = object :
//                MyDownloadListener(SoftReference<Any?>(this)) {
//                override fun onRefresh() {
//                    if (userTag != null && userTag.get() != null) {
//                        val viewHolder: ViewHolder = userTag.get() as ViewHolder
                        when (downloadInfo!!.status) {

                            DownloadInfo.STATUS_NONE -> {
                                holder.progressBar.progress = 0
                                holder.progressTextView.text = "0%"
                                downloadManager.resume(downloadInfo)

                            }
                            DownloadInfo.STATUS_PAUSED , DownloadInfo.STATUS_ERROR -> {


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


                                handler = Handler(Looper.myLooper()!!)
                                runnable = Runnable {
                                    holder.progressBar.progress =
                                        (downloadInfo!!.progress * 100.0 / downloadInfo!!.size).toInt()

                                    holder.progressTextView.text =
                                        formatFileSize(downloadInfo!!.progress * 100.0 / downloadInfo!!.size) + "%"
                                }
                                handler.postDelayed(runnable,200)





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
                                downloadInfo = null

                                DownloandingFragment.musicList = musicList



                            }
                        }
//                    }
//                }
//            }
            downloadManager.download(downloadInfo)


        }


        holder.title.text = musicList[position].name
        holder.article.text = musicList[position].artistName

        Glide.with(context)
            .load(musicList[position].image)
            .apply(
                RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).centerCrop()
            )
            .into(holder.image)

        holder.option.setOnClickListener {
            showBottomSheetDialogAdapter(music2, position)
        }


    }


    override fun getItemCount(): Int {
        return musicList.size
    }

    private fun notifyDownloadStatus(position: Int) {
        if (downloadInfo == null) {
            return
        }
        if (downloadInfo!!.status == DownloadInfo.STATUS_REMOVED) {
            try {
                musicList.removeAt(position)
                DownloandingFragment.musicList = musicList
                notifyDataSetChanged()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
    }

    fun showBottomSheetDialogAdapter(data: Data, position: Int) {

        val bottomSheet = BottomSheetDialog(context)
        bottomSheet.setContentView(R.layout.bottomsheet_download_option)

        val pauseDownload = bottomSheet.findViewById<View>(R.id.ln_pause_download)
        val removeDownload = bottomSheet.findViewById<View>(R.id.ln_remove_downloading)
        val addShareSong = bottomSheet.findViewById<View>(R.id.ln_share_song)


        pauseDownload?.setOnClickListener {

            bottomSheet.dismiss()
        }

        removeDownload?.setOnClickListener {

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