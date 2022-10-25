package com.ddona.music_download_ms3_tunk.ui.activity

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ddona.music_download_ms3_tunk.R
import com.ddona.music_download_ms3_tunk.api.SongApi
import com.ddona.music_download_ms3_tunk.databinding.ActivityMainBinding
import com.ddona.music_download_ms3_tunk.ui.fragment.ChangeRegionFragment
import com.ddona.music_download_ms3_tunk.user_case.UseCases
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.model.exitApplication
import com.ddona.music_download_ms3_tunk.model.playlistMusic
import com.ddona.music_download_ms3_tunk.viewmodel.SongViewModel
import com.example.newsapp.fragments.FavouriteFragment
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration


    @Inject
    lateinit var api: SongApi

    @Inject
    lateinit var useCases: UseCases

    val viewModel: SongViewModel by viewModels()

    companion object {
        lateinit var binding: ActivityMainBinding
        lateinit var MusicListMA: ArrayList<Data>

        var playlistList: ArrayList<playlistMusic> = ArrayList()


        var sortOrder: Int = 0
        val sortingList = arrayOf(
            MediaStore.Audio.Media.DATE_ADDED + " DESC", MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.SIZE + " DESC"
        )
        var permssion = 0

    }

     private val requestPermissionLaucher = registerForActivityResult(ActivityResultContracts.RequestPermission()){

        permssion = if(it){
            1
        }

        else{
            0
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionLaucher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)





        initializeLayout()

        val navHostFragment =
            supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment


        navController = navHostFragment.findNavController()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.searchFragment || destination.id == R.id.changeRegionFragment) {
                binding.bottomNav.visibility = View.GONE
            } else {

                binding.bottomNav.visibility = View.VISIBLE
            }
        }
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.downloadFragment,
                R.id.playlistFragment,
                R.id.settingFragment
            )
        )


        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.bottomNav.setupWithNavController(navController)


        if (requestRuntimePermission()) {
            //for retrieving favourites data using shared preferences
            FavouriteFragment.favouriteList = ArrayList()
            val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE)
            val jsonString = editor.getString("FavouriteSongs", null)
            val typeToken = object : TypeToken<ArrayList<Data>>() {}.type
            if (jsonString != null) {
                val data: ArrayList<Data> =
                    GsonBuilder().create().fromJson(jsonString, typeToken)
                FavouriteFragment.favouriteList.addAll(data)
            }

            val pref = getSharedPreferences("COUNTRY", MODE_PRIVATE)
            val data1 = pref.getString("id_country", null)
            val data2 = pref.getString("name_country", null)
            val data3 = pref.getInt("flag_country", R.drawable.ic_language_select)
            if (data1 != null && data2 != null) {
                ChangeRegionFragment.id_country = data1
                ChangeRegionFragment.name_country = data2
                ChangeRegionFragment.flag_country = data3

            }

        }

    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        exitApplication()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onResume() {
        super.onResume()
        //for storing favourites data using shared preferences
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavouriteFragment.favouriteList)
        editor.putString("FavouriteSongs", jsonString)
        editor.apply()

        if (PlayerActivity.musicService != null) {
            binding.nowPlaying.visibility = View.VISIBLE
        }

        viewModel.playlistmusicList.observe(this@MainActivity) {
            if (it != playlistList) {
                playlistList = ArrayList()
                playlistList.addAll(it)
            }



        }

        Log.d("SDfdsf", "onCreate: $playlistList")

    }

    private fun requestRuntimePermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                13
            )
            return false
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n")
    private fun initializeLayout() {
        val sortEditor = getSharedPreferences("SORTING", MODE_PRIVATE)
        sortOrder = sortEditor.getInt("sortOrder", 0)
        MusicListMA = getAllAudio()


        //for refreshing layout on swipe from top
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun getAllAudio(): ArrayList<Data> {
        val tempList = ArrayList<Data>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
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
        val cursor = this.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null,
            sortingList[sortOrder], null
        )
        if (cursor != null) {
            if (cursor.moveToFirst())
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
                        image = image,
                        name = name
                    )
                    val file = File(music.audio)
                    if (file.exists())
                        tempList.add(music)
                } while (cursor.moveToNext())
            cursor.close()
        }
        return tempList
    }

    override fun onPause() {
        super.onPause()

        val pref = getSharedPreferences("COUNTRY", MODE_PRIVATE).edit()
        pref.putString("id_country", ChangeRegionFragment.id_country)
        pref.putString("name_country", ChangeRegionFragment.name_country)
        pref.putInt("flag_country", ChangeRegionFragment.flag_country)
        pref.apply()
    }

}