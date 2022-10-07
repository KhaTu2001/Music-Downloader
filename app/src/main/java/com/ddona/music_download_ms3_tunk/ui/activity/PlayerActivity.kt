package com.ddona.music_download_ms3_tunk.ui.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.LoudnessEnhancer
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ddona.music_download_ms3_tunk.R
import com.ddona.music_download_ms3_tunk.adapter.PlaylistViewAdapter
import com.ddona.music_download_ms3_tunk.databinding.ActivityPlayerBinding
import com.ddona.music_download_ms3_tunk.databinding.CreateNewPlaylistDialogBinding
import com.ddona.music_download_ms3_tunk.fr.SearchFragment
import com.ddona.music_download_ms3_tunk.model.*
import com.ddona.music_download_ms3_tunk.service.MusicService
import com.ddona.music_download_ms3_tunk.ui.fragment.*
import com.ddona.music_download_ms3_tunk.viewmodel.SongViewModel
import com.example.newsapp.fragments.FavouriteFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tankery.lib.circularseekbar.CircularSeekBar


@AndroidEntryPoint
class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {
    val viewModel: SongViewModel by viewModels()
    var Offline: Boolean = false
    var playoneTime: Boolean = false
    private lateinit var adapter: PlaylistViewAdapter
    private lateinit var bottomSheet2: BottomSheetDialog
    private  var music : Data? = null

    companion object {
        lateinit var musicList: ArrayList<Data>
        lateinit var oldmusicList: ArrayList<Data>
        lateinit var binding: ActivityPlayerBinding

        var isPlaying: Boolean = false
        var isShuffer: Boolean = false
        var isFavourite: Boolean = false
        var fIndex: Int = -1
        var PlaylistIndex: Int = -1


        var isClickNextPrev = false
        var musicService: MusicService? = null
        var songPosition = 0
        var repeat: Boolean = false
        var repeatOneSong: Boolean = false
        var nowPlayingId: String = ""
        lateinit var loudnessEnhancer: LoudnessEnhancer
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)




        initializeLayout()

        music = musicList[songPosition]

        adapter = PlaylistViewAdapter(
            this, playlistList = MyMusicFragment.musicPlaylist.ref, true,music)

        bottomSheet2 = BottomSheetDialog(this)
        bottomSheet2.setContentView(R.layout.bottom_sheet_add_playlist)
        val binder = bottomSheet2.findViewById<RecyclerView>(R.id.rv_new_playlist)
        binder?.setHasFixedSize(true)
        binder?.adapter = adapter


        setContentView(binding.root)

        if (intent.getStringExtra("from") == "MusicAdapter") {
            Offline = true
            binding.addToPlaylist.visibility = View.VISIBLE
            binding.addToFavorite.visibility = View.GONE
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.addToFavorite.setOnClickListener {
            fIndex = favouriteChecker(musicList[songPosition].id)
            if (isFavourite) {
                isFavourite = false
                binding.addToFavorite.setImageResource(R.drawable.ic_add_to_favorite)
                FavouriteFragment.favouriteList.removeAt(fIndex)
            } else {
                isFavourite = true
                binding.addToFavorite.setImageResource(R.drawable.ic_add_to_favorite_active)
                FavouriteFragment.favouriteList.add(musicList[songPosition])
            }
            FavouriteFragment.favouritesChanged = true
        }

        binding.shareSong.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicList[songPosition].audio))
            startActivity(Intent.createChooser(shareIntent, "Sharing Music File!!"))
        }

        binding.playPauseBtn.setOnClickListener {

            if (isPlaying) pauseMusic()
            else playMusic()
        }
        binding.nextBtn.setOnClickListener {
            prevNextSong(incerment = true, isClickNextPrev = true)
        }

        binding.previousBtn.setOnClickListener {
            prevNextSong(incerment = false, isClickNextPrev = true)
        }

        binding.shufferBtn.setOnClickListener {
            if (isShuffer) inshufferMusic() else shufferMusic()
        }

        binding.repeatBtn.setOnClickListener {
            if (!repeat && !repeatOneSong) {
                binding.repeatBtn.setImageResource(R.drawable.ic_loop_song)
                repeat = true
            } else if (repeat && !repeatOneSong) {
                binding.repeatBtn.setImageResource(R.drawable.ic_loop_onetime)
                repeatOneSong = true
            } else {
                binding.repeatBtn.setImageResource(R.drawable.ic_loop_song_inactive)
                repeat = false
                repeatOneSong = false
            }
        }

        binding.addOptionMenu.setOnClickListener {
            showBottomSheetDialog()
        }
        binding.seekBar.setOnSeekBarChangeListener(object :
            CircularSeekBar.OnCircularSeekBarChangeListener {
            override fun onProgressChanged(
                circularSeekBar: CircularSeekBar?,
                progress: Float,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    musicService!!.mediaPlayer!!.seekTo(progress.toInt())
                    musicService!!.showNotification(if (isPlaying) R.drawable.ic_pause_song_icon else R.drawable.ic_play_song_icon)
                }
            }

            override fun onStopTrackingTouch(seekBar: CircularSeekBar?) = Unit

            override fun onStartTrackingTouch(seekBar: CircularSeekBar?) = Unit
        })

        binding.addToPlaylist.setOnClickListener {
            showAddToPlaylistBottomSheet()
        }


    }

    override fun onStart() {
        super.onStart()
        Log.d("zzzzzw", "onStart")
    }

    private fun initializeLayout() {
        songPosition = intent.getIntExtra("index", 0)
        when (intent.getStringExtra("from")) {

            "NowPlaying" -> {
                setLayout()
                binding.tvSeekBarStart.text =
                    formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.tvSeekBarEnd.text =
                    formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
                binding.seekBar.progress = musicService!!.mediaPlayer!!.currentPosition.toFloat()
                binding.seekBar.max = musicService!!.mediaPlayer!!.duration.toFloat()
                if (isPlaying) binding.playPauseBtn.setImageResource(R.drawable.ic_pause_song)
                else binding.playPauseBtn.setImageResource(R.drawable.ic_play_pause_song)
            }
            "Downloaded" -> initServiceAndPlaylist(HomeFragment.downloadListSong)
            "Listened" -> initServiceAndPlaylist(HomeFragment.topListSong)
            "Trending" -> initServiceAndPlaylist(HomeFragment.trendingListSong)
            "Genres" -> initServiceAndPlaylist(SeeAllSongByGenreFragment.topListSongByGenre)
            "SeeAllSong" -> initServiceAndPlaylist(SeeAllSongFragment.localListSong)
            "FavouriteAdapter" -> initServiceAndPlaylist(FavouriteFragment.favouriteList)
            "MusicAdapter" -> initServiceAndPlaylist(MainActivity.MusicListMA)
            "SearchFragment" -> initServiceAndPlaylist(SearchFragment.searchListMA)


        }
        if (musicService != null && !isPlaying) playMusic()

    }

    private fun setLayout() {
        Log.d("zzzzzw", "$songPosition")

        fIndex = favouriteChecker(musicList[songPosition].id)
        Glide.with(applicationContext)
            .load(musicList[songPosition].image)
            .apply(
                RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).centerCrop()
            )
            .into(binding.songImg)
        Glide.with(applicationContext)
            .load(musicList[songPosition].image)
            .apply(
                RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).centerCrop()
            )
            .into(binding.backgroundSongImg)
        binding.songNamed.text = musicList[songPosition].name
        binding.authorName.text = musicList[songPosition].artistName

        if (isShuffer) binding.shufferBtn.setImageResource(R.drawable.ic_shuffer_song_active)
        else binding.shufferBtn.setImageResource(R.drawable.ic_shuffer_song_inactive)

        if (isFavourite) binding.addToFavorite.setImageResource(R.drawable.ic_add_to_favorite_active)
        else binding.addToFavorite.setImageResource(R.drawable.ic_add_to_favorite)

        if (repeat) binding.repeatBtn.setImageResource(R.drawable.ic_loop_song)

        if (repeat && repeatOneSong) binding.repeatBtn.setImageResource(R.drawable.ic_loop_onetime)

        if (!repeat && !repeatOneSong) binding.repeatBtn.setImageResource(R.drawable.ic_loop_song_inactive)


    }


    private fun createMediaPlayer() {

        try {
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicList[songPosition].audio)
            musicService!!.mediaPlayer!!.prepare()
            binding.tvSeekBarStart.text =
                formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.tvSeekBarEnd.text =
                formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.seekBar.progress = 0F
            binding.seekBar.max = musicService!!.mediaPlayer!!.duration.toFloat()
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
            nowPlayingId = musicList[songPosition].id
            playMusic()
            animationRotate(true)
            if (musicList.size - 1 == songPosition && playoneTime) {
                pauseMusic()
            }
            playoneTime = false
            loudnessEnhancer = LoudnessEnhancer(musicService!!.mediaPlayer!!.audioSessionId)
            loudnessEnhancer.enabled = true
        } catch (e: Exception) {
            return
        }
    }


    private fun animationRotate(isPlaying: Boolean) {
        val anim = AnimationUtils.loadAnimation(this, R.anim.rotate_song_img)
        anim.fillAfter = true
        if (isPlaying) {
            anim.start()
        }

    }


    private fun playMusic() {
        binding.playPauseBtn.setImageResource(R.drawable.ic_pause_song)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
        musicService!!.showNotification(R.drawable.ic_pause_song_icon)
    }

    private fun pauseMusic() {
        binding.playPauseBtn.setImageResource(R.drawable.ic_play_pause_song)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
        musicService!!.showNotification(R.drawable.ic_play_song_icon)
    }

    private fun shufferMusic() {
        binding.shufferBtn.setImageResource(R.drawable.ic_shuffer_song_active)
        isShuffer = true
        setLayout()
    }

    private fun inshufferMusic() {
        binding.shufferBtn.setImageResource(R.drawable.ic_shuffer_song_inactive)
        isShuffer = false
        setLayout()

    }

    private fun prevNextSong(incerment: Boolean, isClickNextPrev: Boolean) {
        if (incerment && isClickNextPrev) {
            setSongPosition(increment = true, isClickNextPrev = true)
            if (isShuffer) {
                shufferPosition(true)
                shufferMusic()
            } else {
                shufferPosition(false)
                inshufferMusic()
            }
            setLayout()
            createMediaPlayer()
        } else {
            setSongPosition(increment = false, isClickNextPrev = true)
            if (isShuffer) {
                shufferPosition(true)
                shufferMusic()
            } else {
                shufferPosition(false)
                inshufferMusic()
            }
            setLayout()
            createMediaPlayer()
        }

    }


    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        if (musicService == null) {
            val binder = service as MusicService.MyBinder
            musicService = binder.currentService()
            musicService!!.audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            musicService!!.audioManager.requestAudioFocus(
                musicService,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
        createMediaPlayer()
        musicService!!.seekBarSetup()
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(p0: MediaPlayer?) {
        setSongPosition(increment = true, isClickNextPrev = false)

        if (!repeat && !repeatOneSong) {
            if (musicList.size - 1 == songPosition) {
                playoneTime = true
            }
        }

        if (isShuffer) {
            shufferPosition(true)
            shufferMusic()
        } else {
            shufferPosition(false)
            inshufferMusic()
        }
        createMediaPlayer()
        setLayout()

        //for refreshing now playing image & text on song completion
        NowPlaying.binding.songNamed.isSelected = true
        NowPlaying.binding.authorName.isSelected = true
        Glide.with(applicationContext)
            .load(musicList[songPosition].image)
            .apply(
                RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).centerCrop()
            )
            .into(NowPlaying.binding.songImg)
        NowPlaying.binding.songNamed.text = musicList[songPosition].name
        NowPlaying.binding.authorName.text = musicList[songPosition].artistName

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 || resultCode == RESULT_OK)
            return
    }

    override fun onDestroy() {
        super.onDestroy()
        if (musicList[songPosition].id == "Unknown" && !isPlaying) {
            exitApplication()
        }
    }

    private fun initServiceAndPlaylist(playlist: ArrayList<Data>) {
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        startService(intent)

        musicList = ArrayList()
        oldmusicList = ArrayList()
        musicList.addAll(playlist)
        oldmusicList.addAll(playlist)
        setLayout()
    }


    fun showBottomSheetDialog() {
        val bottomSheet: BottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetStyle)
        bottomSheet.setContentView(R.layout.add_option_dialog)


        val addPlaylist = bottomSheet.findViewById<View>(R.id.ln_add_to_playlist)
        val addFavorite = bottomSheet.findViewById<View>(R.id.ln_add_to_favorite)
        val addDownload = bottomSheet.findViewById<View>(R.id.ln_add_to_download)


        if (Offline == true) {
            addDownload?.visibility = View.GONE
        }


        addPlaylist?.setOnClickListener()
        {
            showAddToPlaylistBottomSheet()
        }
        var txtAddFavorite = bottomSheet.findViewById<TextView>(R.id.txt_add_to_favorite)

        if (isFavourite) txtAddFavorite?.setText("Remove from Favorite")
        else txtAddFavorite?.setText("Add to Favorite")

        addFavorite?.setOnClickListener()
        {
            fIndex = favouriteChecker(musicList[songPosition].id)

            if (isFavourite) {
                isFavourite = false
                binding.addToFavorite.setImageResource(R.drawable.ic_add_to_favorite)
                FavouriteFragment.favouriteList.removeAt(fIndex)

            } else {
                isFavourite = true
                binding.addToFavorite.setImageResource(R.drawable.ic_add_to_favorite_active)
                FavouriteFragment.favouriteList.add(musicList[songPosition])

            }
            FavouriteFragment.favouritesChanged = true
            bottomSheet.dismiss()
        }
        addDownload?.setOnClickListener()
        {

            bottomSheet.dismiss()
        }

        bottomSheet.show()
    }

    fun showAddToPlaylistBottomSheet() {

        val createPLBtn = bottomSheet2.findViewById<View>(R.id.ln_create_playlist)

        createPLBtn?.setOnClickListener {
            customAlertDialog()

        }
        bottomSheet2.dismiss()
        bottomSheet2.show()

    }

    fun customAlertDialog() {
        val customDialog = LayoutInflater.from(this)
            .inflate(R.layout.create_new_playlist_dialog, binding.root, false)
        val binder = CreateNewPlaylistDialogBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(this)
        val dialog = builder.setView(customDialog)
            .create()
        val playlistName = binder.edtNamePlaylist.text
        val submitBtn = binder.createPlaylistBtn
        val cancelBtn = binder.cancelBtn



        submitBtn.setOnClickListener {
            if (playlistName != null)
                if (playlistName.isNotEmpty()) {
                    addPlaylist(playlistName.toString(), this)

                }
            dialog.dismiss()
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.setCanceledOnTouchOutside(false);
        dialog.window?.setGravity(Gravity.TOP)

    }

    fun addPlaylist(name: String, context: Context) {
        var playlistExists = false
        for (i in MyMusicFragment.musicPlaylist.ref) {
            if (name == i.name) {
                playlistExists = true
                break
            }
        }

        val customDialogsuccess = LayoutInflater.from(context)
            .inflate(R.layout.create_playlist_successfully, binding.root, false)
        val build = MaterialAlertDialogBuilder(context)
        val dialogsuccess = build.setView(customDialogsuccess)
            .create()


        if (playlistExists) Toast.makeText(context, "Playlist Exist!!", Toast.LENGTH_SHORT).show()
        else {
            val tempPlaylist = Playlist()
            tempPlaylist.name = name
            tempPlaylist.playlist = ArrayList()
            MyMusicFragment.musicPlaylist.ref.add(tempPlaylist)
            adapter.refreshPlaylist()

            dialogsuccess.show()
            dialogsuccess.window?.setGravity(Gravity.TOP)

            MainScope().launch {
                delay(2000)
                dialogsuccess.dismiss()
            }
        }
    }


}
