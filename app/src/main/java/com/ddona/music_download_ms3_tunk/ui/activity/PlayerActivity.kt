package com.ddona.music_download_ms3_tunk.ui.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.LoudnessEnhancer
import android.os.Bundle
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ddona.music_download_ms3_tunk.R
import com.ddona.music_download_ms3_tunk.adapter.PlaylistAdapter
import com.ddona.music_download_ms3_tunk.callback.ListSongItemClick
import com.ddona.music_download_ms3_tunk.databinding.ActivityPlayerBinding
import com.ddona.music_download_ms3_tunk.databinding.CreateNewPlaylistDialogBinding
import com.ddona.music_download_ms3_tunk.fr.SearchFragment
import com.ddona.music_download_ms3_tunk.model.*
import com.ddona.music_download_ms3_tunk.service.MusicService
import com.ddona.music_download_ms3_tunk.ui.fragment.*
import com.ddona.music_download_ms3_tunk.user_case.UseCases
import com.ddona.music_download_ms3_tunk.ui.fragment.FavouriteFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import me.tankery.lib.circularseekbar.CircularSeekBar
import java.lang.Runnable
import javax.inject.Inject


@AndroidEntryPoint
class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener,
    ListSongItemClick,
    Runnable {

    @Inject
    lateinit var usercase: UseCases


    var playoneTime: Boolean = false
    private lateinit var bottomSheet2: BottomSheetDialog
    private lateinit var adapter: PlaylistAdapter

    companion object {
        lateinit var musicList: ArrayList<Data>
        lateinit var oldmusicList: ArrayList<Data>
        lateinit var binding: ActivityPlayerBinding

        var isPlaying: Boolean = false
        var isShuffer: Boolean = false
        var isFavourite: Boolean = false
        var isBlock: Boolean = false

        var fIndex: Int = -1

        var checkFlag: Boolean = false

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
        checkFlag = true

        adapter = PlaylistAdapter(this, usercase, true, this, null)


        if (intent.getStringExtra("from") == "MusicAdapter") {
            binding.addToPlaylist.visibility = View.VISIBLE
            binding.addToFavorite.visibility = View.GONE
            lifecycleScope.launch {
                usercase.getAllPlaylist(1).collect {
                    adapter.submitList(it)
                }
            }
        } else {
            lifecycleScope.launch {
                usercase.getAllPlaylist(0).collect {
                    adapter.submitList(it)
                }
            }
        }




        initializeLayout()

        bottomSheet2 = BottomSheetDialog(this, R.style.BottomSheetStyle)
        bottomSheet2.setContentView(R.layout.bottom_sheet_add_playlist)
        val binder = bottomSheet2.findViewById<RecyclerView>(R.id.rv_new_playlist)
        binder?.setHasFixedSize(true)
        binder?.adapter = adapter

        setContentView(binding.root)



        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.addToFavorite.setOnClickListener {
            fIndex = favouriteChecker(musicList[songPosition].id)
            if (isFavourite) {
                isFavourite = false
                binding.addToFavorite.setImageResource(R.drawable.ic_add_to_favorite)
                FavouriteFragment.favouriteList.removeAt(fIndex)
                Toast.makeText(
                    this,
                    "Song Remove from Favourite successfully!!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                isFavourite = true
                binding.addToFavorite.setImageResource(R.drawable.ic_add_to_favorite_active)
                FavouriteFragment.favouriteList.add(musicList[songPosition])
                Toast.makeText(this, "Song added to Favourite successfully!!", Toast.LENGTH_SHORT)
                    .show()
            }
            FavouriteFragment.favouritesChanged = true
        }

        binding.shareSong.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, musicList[songPosition].audio)
            startActivity(Intent.createChooser(shareIntent, "Sharing Music Link!!"))
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
        startAnimationRotate()

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
            "FavouriteAdapter" -> intent.getParcelableArrayListExtra<Data>("FSongMusic")
                ?.let { initServiceAndPlaylist(it) }
            "MusicAdapter" -> initServiceAndPlaylist(DownloadedFragment.musicListMA)
            "SearchFragment" -> initServiceAndPlaylist(SearchFragment.searchListMA)
            "playlistDetails" -> intent.getParcelableArrayListExtra<Data>("dataMusic")
                ?.let { initServiceAndPlaylist(it) }
        }
        if (musicService != null && !isPlaying) playMusic()

    }

    private fun setLayout() {


        Glide.with(applicationContext)
            .load(musicList[songPosition].image)
            .apply(
                RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen)
                    .centerCrop()
            )
            .into(binding.songImg)
        Glide.with(applicationContext)
            .load(musicList[songPosition].image)
            .apply(
                RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen)
                    .centerCrop()
            )
            .into(binding.backgroundSongImg)
        binding.songNamed.text = musicList[songPosition].name
        binding.authorName.text = musicList[songPosition].artistName




        if (isShuffer) binding.shufferBtn.setImageResource(R.drawable.ic_shuffer_song_active)
        else binding.shufferBtn.setImageResource(R.drawable.ic_shuffer_song_inactive)

        if (isFavourite) binding.addToFavorite.setImageResource(R.drawable.ic_add_to_favorite_active)
        else binding.addToFavorite.setImageResource(R.drawable.ic_add_to_favorite)

        if (repeat) binding.repeatBtn.setImageResource(R.drawable.ic_loop_song)

        if (repeat && repeatOneSong) {
            binding.repeatBtn.setImageResource(R.drawable.ic_loop_onetime)
        }

        if (!repeat && !repeatOneSong) binding.repeatBtn.setImageResource(R.drawable.ic_loop_song_inactive)


    }


    private fun createMediaPlayer() {
        try {

            if (musicService!!.mediaPlayer == null)
                musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicList[songPosition].audio)

            musicService!!.mediaPlayer!!.prepare()

            musicService!!.mediaPlayer!!.setOnCompletionListener(this@PlayerActivity)


            binding.tvSeekBarStart.text =
                formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.tvSeekBarEnd.text =
                formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.seekBar.progress = 0F
            binding.seekBar.max = musicService!!.mediaPlayer!!.duration.toFloat()
            nowPlayingId = musicList[songPosition].id

            playMusic()

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


    private fun startAnimationRotate() {
        binding.songImg.animate().rotationBy(360F).withEndAction(this).setDuration(5000)
            .setInterpolator(LinearInterpolator()).start()

    }

    private fun stopAnimation() {
        binding.songImg.animate().cancel()
    }

    override fun run() {
        if (isPlaying) {
            binding.songImg.animate().rotationBy(360F).withEndAction(this).setDuration(5000)
                .setInterpolator(LinearInterpolator()).start()
        } else {
            binding.songImg.animate().cancel()
        }


    }


    private fun playMusic() {
        binding.playPauseBtn.setImageResource(R.drawable.ic_pause_song)
        isPlaying = true
        startAnimationRotate()
        musicService!!.mediaPlayer!!.start()
        musicService!!.showNotification(R.drawable.ic_pause_song_icon)

    }

    private fun pauseMusic() {
        binding.playPauseBtn.setImageResource(R.drawable.ic_play_pause_song)
        isPlaying = false
        stopAnimation()
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

        if (!isBlock) {
            isBlock = true

            if (incerment && isClickNextPrev) {
                setSongPosition(increment = true, isClickNextPrev = true)

            } else {
                setSongPosition(increment = false, isClickNextPrev = true)

            }

            if (isShuffer) {
                shufferPosition(true)
                shufferMusic()
            } else {
                shufferPosition(false)
                inshufferMusic()
            }

            setLayout()

            createMediaPlayer()

            MainScope().launch {
                delay(1379)
                isBlock = false
            }

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


        createMediaPlayer()


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


    private fun showBottomSheetDialog() {
        val bottomSheet: BottomSheetDialog = BottomSheetDialog(this)
        bottomSheet.setContentView(R.layout.add_option_dialog)


        val addPlaylist = bottomSheet.findViewById<View>(R.id.ln_add_to_playlist)
        val addFavorite = bottomSheet.findViewById<View>(R.id.ln_add_to_favorite)
        val addDownload = bottomSheet.findViewById<View>(R.id.ln_add_to_download)

        if (intent.getStringExtra("from") == "MusicAdapter")
            addDownload?.visibility = View.GONE


        addPlaylist?.setOnClickListener()
        {
            showAddToPlaylistBottomSheet()
        }
        val txtAddFavorite = bottomSheet.findViewById<TextView>(R.id.txt_add_to_favorite)

        if (isFavourite) txtAddFavorite?.text = "Remove from Favorite"
        else txtAddFavorite?.text = "Add to Favorite"

        addFavorite?.setOnClickListener()
        {
            fIndex = favouriteChecker(musicList[songPosition].id)

            if (isFavourite) {
                isFavourite = false
                binding.addToFavorite.setImageResource(R.drawable.ic_add_to_favorite)
                FavouriteFragment.favouriteList.removeAt(fIndex)
                Toast.makeText(
                    this,
                    "Song Remove from Favourite successfully!!",
                    Toast.LENGTH_SHORT
                ).show()


            } else {
                isFavourite = true
                binding.addToFavorite.setImageResource(R.drawable.ic_add_to_favorite_active)
                FavouriteFragment.favouriteList.add(musicList[songPosition])
                Toast.makeText(this, "Song added to Favourite successfully!!", Toast.LENGTH_SHORT)
                    .show()


            }
            FavouriteFragment.favouritesChanged = true
            bottomSheet.dismiss()
        }
        addDownload?.setOnClickListener()
        {

            DownloandingFragment.musicList.add(musicList[songPosition])

            finish()

            MainActivity.binding.navHostFragment.findNavController().navigate(R.id.playlistFragment)


            Toast.makeText(this, "Is Downloading", Toast.LENGTH_LONG).show()
            bottomSheet.dismiss()
        }

        CoroutineScope(Dispatchers.Main).launch {
            usercase.checkID.invoke(musicList[songPosition].id).collect {
                if (it > 0) {
                    addDownload?.visibility = View.GONE

                }
            }
        }


        bottomSheet.show()
    }

    private fun showAddToPlaylistBottomSheet() {

        val createPLBtn = bottomSheet2.findViewById<View>(R.id.ln_create_playlist)
        val searchText =
            bottomSheet2.findViewById<EditText>(R.id.edt_search)

        var job: Job? = null

        if (intent.getStringExtra("from") == "MusicAdapter") {

            searchText?.addTextChangedListener { editable ->
                job?.cancel()
                job = MainScope().launch {
                    editable?.let {
                        if (editable.toString().isNotEmpty()) {
                            CoroutineScope(Dispatchers.IO).launch {
                                usercase.getAllPlaylistByName.invoke(editable.toString(), 1)
                                    .collect {
                                        adapter.submitList(it)
                                    }
                            }
                        } else {
                            lifecycleScope.launch {
                                usercase.getAllPlaylist(0).collect {
                                    adapter.submitList(it)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            searchText?.addTextChangedListener { editable ->
                job?.cancel()
                job = MainScope().launch {
                    editable?.let {
                        if (editable.toString().isNotEmpty()) {
                            CoroutineScope(Dispatchers.IO).launch {
                                usercase.getAllPlaylistByName.invoke(editable.toString(), 0)
                                    .collect {
                                        adapter.submitList(it)
                                    }
                            }
                        } else {
                            lifecycleScope.launch {
                                usercase.getAllPlaylist(0).collect {
                                    adapter.submitList(it)
                                }
                            }
                        }
                    }
                }
            }
        }


        createPLBtn?.setOnClickListener {
            customAlertDialog()

        }
        bottomSheet2.dismiss()
        bottomSheet2.show()

    }

    private fun customAlertDialog() {
        val customDialog = LayoutInflater.from(this)
            .inflate(R.layout.create_new_playlist_dialog, binding.root, false)
        val binder = CreateNewPlaylistDialogBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(this, R.style.full_screen_dialog)
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
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setGravity(Gravity.TOP)

    }

    fun addPlaylist(name: String, context: Context) {
        var playlistExists = false
        for (i in MainActivity.playlistListOnl) {
            if (name == i.playlistName) {
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

            val tempPlaylist = playlistMusic(
                playlistName = name, playList_ID = null, status = 0
            )

            lifecycleScope.launch {
                usercase.addPlaylist.invoke(tempPlaylist)
            }


            dialogsuccess.show()
            dialogsuccess.window?.setGravity(Gravity.TOP)

            MainScope().launch {
                delay(1000)
                dialogsuccess.dismiss()
            }
        }
    }

    override fun ListSongOnClick(index: Int) {
        val music = Data(
            id = musicList[songPosition].id,
            audioDownload = musicList[songPosition].audioDownload,
            albumId = musicList[songPosition].albumId,
            albumName = musicList[songPosition].albumName,
            artistId = musicList[songPosition].artistId,
            audio = musicList[songPosition].audio,
            artistName = musicList[songPosition].artistName,
            duration = musicList[songPosition].duration,
            image = musicList[songPosition].image,
            name = musicList[songPosition].name,
            playlist_id = index,
            status = 0
        )
        lifecycleScope.launch {
            usercase.addMusic(music)
        }
        Toast.makeText(this, "Song Added successfully!!", Toast.LENGTH_SHORT).show()
    }


}
