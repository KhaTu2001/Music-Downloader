package com.ddona.music_download_ms3_tunk.ui.fragment

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.ddona.music_download_ms3_tunk.adapter.MusicAdapter
import com.ddona.music_download_ms3_tunk.databinding.FragmentDownloadedBinding
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.model.comparePaths
import com.ddona.music_download_ms3_tunk.user_case.UseCases
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class DownloadedFragment : Fragment() {

    @Inject
    lateinit var useCases: UseCases

    private lateinit var binding: FragmentDownloadedBinding
    private lateinit var musicAdapter: MusicAdapter

    companion object {
        lateinit var musicListMA: ArrayList<Data>

        var sortOrder: Int = 0
        val sortingList = arrayOf(
            MediaStore.Audio.Media.DATE_ADDED + " DESC",
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.SIZE + " DESC",
            MediaStore.Audio.Media.MIME_TYPE + "mp3",
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDownloadedBinding.inflate(inflater)

        return binding.root

    }

    override fun onResume() {
        super.onResume()
        val file = File(Environment.getExternalStoragePublicDirectory("Music Downloader").path)
        file.listFiles()?.forEach { musicFile ->
            if (musicFile.length() != 0L && !comparePaths(musicFile.path)) {
                MediaScannerConnection.scanFile(
                    requireContext(), listOf(musicFile.path).toTypedArray(), null, null
                )
            }
        }


        initializeLayout()
        binding.RvAllMusic.setHasFixedSize(true)
        musicAdapter = MusicAdapter(requireContext(), musicListMA, useCases)
        binding.RvAllMusic.adapter = musicAdapter
    }



    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n")
    private fun initializeLayout() {
        musicListMA = getAllAudio()
    }


    @RequiresApi(Build.VERSION_CODES.R)
    fun getAllAudio(): ArrayList<Data> {
        val tempList = ArrayList<Data>()

        val selection =
            MediaStore.Audio.Media.DATA + " LIKE ? AND " + MediaStore.Audio.Media.DATA + " NOT LIKE ? "
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )

        val selectionArgs = arrayOf("%Music Downloader%", "%Music Downloader/%/%")
        val cursor = context!!.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs,
            sortingList[sortOrder], null
        )
        if (cursor != null) {
            if (cursor.moveToNext())
                do {
                    val albumId =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                            ?: "Unknown"
                    val albumName =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                            ?: "Unknown"
                    val artistId =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID))
                            ?: "Unknown"
                    val artistName =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                            ?: "Unknown"
                    val audio =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                            ?: "Unknown"
                    val name =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                            ?: "Unknown"
                    val id =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                            ?: "Unknown"
                    val duration =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumIdC =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                            .toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val image = Uri.withAppendedPath(uri, albumIdC).toString()
                    val music = Data(
                        albumId = albumId,
                        albumName = albumName,
                        artistId = artistId,
                        artistName = artistName,
                        audio = audio,
                        duration = duration,
                        id = id,
                        status = 1,
                        image = image,
                        name = name,
                        audioDownload = null,

                        )
                    val file = File(music.audio)
                    if (file.exists()) {
                        tempList.add(music)
                    }

                } while (cursor.moveToNext())
            cursor.close()
        }


        return tempList
    }


}