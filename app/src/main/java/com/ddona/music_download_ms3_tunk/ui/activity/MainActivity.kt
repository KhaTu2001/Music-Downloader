package com.ddona.music_download_ms3_tunk.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.model.exitApplication
import com.ddona.music_download_ms3_tunk.model.playlistMusic
import com.ddona.music_download_ms3_tunk.ui.fragment.ChangeRegionFragment
import com.ddona.music_download_ms3_tunk.ui.fragment.FavouriteFragment
import com.ddona.music_download_ms3_tunk.user_case.UseCases
import com.ddona.music_download_ms3_tunk.viewmodel.SongViewModel
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

        var playlistListOnl: ArrayList<playlistMusic> = ArrayList()
        var playlistListOff: ArrayList<playlistMusic> = ArrayList()


        var sortOrder: Int = 0
        val sortingList = arrayOf(
            MediaStore.Audio.Media.DATE_ADDED + " DESC",
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.SIZE + " DESC",
            MediaStore.Audio.Media.MIME_TYPE + "mp3",
        )
        var permssion = 1
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



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


        if (PlayerActivity.musicService != null) {
            binding.nowPlaying.visibility = View.VISIBLE
        }



        viewModel.playlistmusicListOnl.observe(this@MainActivity) {
            if (it != playlistListOnl) {
                playlistListOnl = ArrayList()
                playlistListOnl.addAll(it)


            }
        }

        viewModel.playlistmusicListOff.observe(this@MainActivity) {
            if (it != playlistListOff) {
                playlistListOff = ArrayList()
                playlistListOff.addAll(it)


            }


        }


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
        MusicListMA = getAllAudio()
        Log.d("DSfdsf", "$MusicListMA ")
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

    override fun onPause() {
        super.onPause()

        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavouriteFragment.favouriteList)
        editor.putString("FavouriteSongs", jsonString)
        editor.apply()

        val pref = getSharedPreferences("COUNTRY", MODE_PRIVATE).edit()
        pref.putString("id_country", ChangeRegionFragment.id_country)
        pref.putString("name_country", ChangeRegionFragment.name_country)
        pref.putInt("flag_country", ChangeRegionFragment.flag_country)
        pref.apply()
    }


    object RingtoneUtils {
        private const val LOG_TAG = "RingtoneUtils"
        fun setRingtone(context: Context, ringtoneUri: Uri, type: Int): Boolean {
            Log.v(LOG_TAG, "Setting Ringtone to: $ringtoneUri")
            if (!hasMarshmallow()) {
                Log.v(
                    LOG_TAG,
                    "On a Lollipop or below device, so go ahead and change device ringtone"
                )
                setActualRingtone(context, ringtoneUri, type)
                return true
            } else if (hasMarshmallow() && canEditSystemSettings(context)) {
                Log.v(
                    LOG_TAG,
                    "On a marshmallow or above device but app has the permission to edit system settings"
                )
                setActualRingtone(context, ringtoneUri, type)
                return true
            } else if (hasMarshmallow() && !canEditSystemSettings(context)) {
                Log.d(
                    LOG_TAG,
                    "On android Marshmallow and above but app does not have permission to" +
                            " edit system settings. Opening the manage write settings activity..."
                )
                startManageWriteSettingsActivity(context)
                Toast.makeText(
                    context,
                    "Please allow app to edit settings so your ringtone can be updated",
                    Toast.LENGTH_LONG
                ).show()
                return false
            }
            return false
        }

        private fun setActualRingtone(context: Context, ringtoneUri: Uri, type: Int) {
            RingtoneManager.setActualDefaultRingtoneUri(context, type, ringtoneUri)

            var message = ""
            if (type == RingtoneManager.TYPE_RINGTONE) {
                message = "Ringtone set success "

            } else if (type == RingtoneManager.TYPE_NOTIFICATION) {
                message = "Notification set success "
            }
            if (RingtoneManager.getActualDefaultRingtoneUri(context, type) == ringtoneUri) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    context,
                    "operation failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        private fun startManageWriteSettingsActivity(context: Context) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            // Passing in the app package here allows the settings app to open the exact app
            intent.data = Uri.parse("package:" + context.applicationContext.packageName)
            // Optional. If you pass in a service context without setting this flag, you will get an exception
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }

        private fun hasMarshmallow(): Boolean {
            // returns true if the device is Android Marshmallow or above, false otherwise
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        private fun canEditSystemSettings(context: Context): Boolean {
            return Settings.System.canWrite(context.applicationContext)
        }
    }

//    audio=/storage/emulated/0/Wish_You_Were_Here_-_the.madpix.project.mp3
//    audio=/storage/emulated/0/Download/Voice Changer/Wish You Were Here-Helium.mp3
}