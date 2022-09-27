package com.ddona.music_download_ms3_tunk.ui.activity

import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
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
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.model.exitApplication
import com.ddona.music_download_ms3_tunk.ui.fragment.MyMusicFragment
import com.ddona.music_download_ms3_tunk.viewmodel.SongViewModel
import com.example.newsapp.fragments.FavouriteFragment
import com.google.gson.GsonBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    @Inject
    lateinit var api:SongApi

    val viewModel: SongViewModel by viewModels()

    companion object{
        val sortingList = arrayOf(MediaStore.Audio.Media.DATE_ADDED + " DESC", MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.SIZE + " DESC")
        var sortOrder: Int = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navHostFragment =
            supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()



        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.downloadFragment,R.id.playlistFragment,R.id.settingFragment)
        )

        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.bottomNav.setupWithNavController(navController)


        lifecycleScope.launch(Dispatchers.IO) {
            val abc = api.getAllTopListend()
            Log.d("esrhreherh", "onCreate: $abc")

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(!PlayerActivity.isPlaying && PlayerActivity.musicService != null){
            exitApplication()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onResume() {
        super.onResume()
        //for storing favourites data using shared preferences
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavouriteFragment.favouriteList)
        editor.putString("FavouriteSongs", jsonString)
        val jsonStringPlaylist = GsonBuilder().create().toJson(MyMusicFragment.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStringPlaylist)
        editor.apply()

    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun getAllAudio(): ArrayList<Data>{
        val tempList = ArrayList<Data>()
        val selection = MediaStore.Audio.Media.IS_MUSIC +  " != 0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID)
        val cursor = this.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,selection,null,
            sortingList[sortOrder], null)
        if(cursor != null){
            if(cursor.moveToFirst())
//                do {
//                    val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))?:"Unknown"
//                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))?:"Unknown"
//                    val albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))?:"Unknown"
//                    val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))?:"Unknown"
//                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
//                    val durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
//                    val albumIdC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
//                    val uri = Uri.parse("content://media/external/audio/albumart")
//                    val artUriC = Uri.withAppendedPath(uri, albumIdC).toString()
//                    val music = Data(id = idC, name = titleC, album = albumC, artist = artistC, path = pathC, duration = durationC,
//                        artUri = artUriC)
//                    val file = File(music.audio)
//                    if(file.exists())
//                        tempList.add(music)
//                }while (cursor.moveToNext())
            cursor.close()
        }
        return tempList
    }



}