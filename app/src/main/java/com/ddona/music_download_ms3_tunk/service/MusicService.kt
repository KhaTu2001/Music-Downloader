package com.ddona.music_download_ms3_tunk.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.AudioManager.FLAG_ALLOW_RINGER_MODES
import android.media.AudioManager.STREAM_NOTIFICATION
import android.media.MediaPlayer
import android.media.audiofx.LoudnessEnhancer
import android.os.*
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import com.ddona.music_download_ms3_tunk.App
import com.ddona.music_download_ms3_tunk.R
import com.ddona.music_download_ms3_tunk.model.formatDuration
import com.ddona.music_download_ms3_tunk.ui.activity.MainActivity
import com.ddona.music_download_ms3_tunk.ui.activity.PlayerActivity
import com.ddona.music_download_ms3_tunk.ui.fragment.NowPlaying
import com.harshRajpurohit.musicPlayer.NotificationReceiver


class MusicService: Service(), AudioManager.OnAudioFocusChangeListener {
    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null

    private lateinit var mediaSession: MediaSessionCompat
    lateinit var audioManager: AudioManager
    private lateinit var runnable: Runnable

    override fun onBind(intent: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext, "My Music")
        return myBinder
    }

    inner class MyBinder : Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    fun showNotification(playPauseBtn: Int){

        val intent = Intent(this, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val prevIntent = Intent(this, NotificationReceiver::class.java).setAction(App.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val playIntent = Intent(this, NotificationReceiver::class.java).setAction(App.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val nextIntent = Intent(this, NotificationReceiver::class.java).setAction(App.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val exitIntent = Intent(this, NotificationReceiver::class.java).setAction(App.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(this, 0, exitIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)


        val notification = NotificationCompat.Builder(this, App.CHANNEL_ID)
            .setContentIntent(contentIntent)
            .setContentTitle(PlayerActivity.musicList[PlayerActivity.songPosition].name)
            .setContentText(PlayerActivity.musicList[PlayerActivity.songPosition].artistName)
            .setSmallIcon(R.drawable.logo)
            .setOnlyAlertOnce(true)
            .setSound(null)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(R.drawable.ic_prev_song, "Previous", prevPendingIntent)
            .addAction(playPauseBtn, "Play", playPendingIntent)
            .addAction(R.drawable.ic_next_song, "Next", nextPendingIntent)
            .addAction(R.drawable.ic_clear_song, "Exit", exitPendingIntent)
            .build()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val playbackSpeed = if(PlayerActivity.isPlaying) 1F else 0F
            mediaSession.setMetadata(
                MediaMetadataCompat.Builder()
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mediaPlayer!!.duration.toLong())
                .build())
            val playBackState = PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer!!.currentPosition.toLong(), playbackSpeed)
                .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                .build()
            mediaSession.setPlaybackState(playBackState)
            mediaSession.setCallback(object: MediaSessionCompat.Callback(){

                //called when headphones buttons are pressed
                //currently only pause or play music on button click
                override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
                    if(PlayerActivity.isPlaying){
                        //pause music
                        PlayerActivity.binding.playPauseBtn.setImageResource(R.drawable.ic_play_pause_song)
                        NowPlaying.binding.PlaypauseBtnNP.setImageResource(R.drawable.ic_play_song_icon)

                        PlayerActivity.isPlaying = false
                        mediaPlayer!!.pause()
                        showNotification(R.drawable.ic_play_song_icon)
                    }else{
                        //play music
                        PlayerActivity.binding.playPauseBtn.setImageResource(R.drawable.ic_pause_song)
                        NowPlaying.binding.PlaypauseBtnNP.setImageResource(R.drawable.ic_pause_song_icon)

                        PlayerActivity.isPlaying = true
                        mediaPlayer!!.start()
                        showNotification(R.drawable.ic_pause_song_icon)
                    }
                    return super.onMediaButtonEvent(mediaButtonEvent)
                }
                override fun onSeekTo(pos: Long) {
                    super.onSeekTo(pos)
                    mediaPlayer!!.seekTo(pos.toInt())
                    val playBackStateNew = PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer!!.currentPosition.toLong(), playbackSpeed)
                        .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                        .build()
                    mediaSession.setPlaybackState(playBackStateNew)
                }
            })
        }

        startForeground(13, notification)

    }

    fun createMediaPlayer(){
        try {
            if (mediaPlayer == null) mediaPlayer = MediaPlayer()
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(PlayerActivity.musicList[PlayerActivity.songPosition].audio)
            mediaPlayer!!.prepare()
            PlayerActivity.binding.playPauseBtn.setImageResource(R.drawable.ic_pause_song)
            showNotification(R.drawable.ic_pause_song_icon)

            PlayerActivity.binding.tvSeekBarStart.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.tvSeekBarEnd.text = formatDuration(mediaPlayer!!.duration.toLong())
            PlayerActivity.binding.seekBar.progress = 0F
            PlayerActivity.binding.seekBar.max = mediaPlayer!!.duration.toFloat()
            PlayerActivity.nowPlayingId = PlayerActivity.musicList[PlayerActivity.songPosition].id
            PlayerActivity.loudnessEnhancer = LoudnessEnhancer(mediaPlayer!!.audioSessionId)
            PlayerActivity.loudnessEnhancer.enabled = true
        }catch (e: Exception){return}
    }
    fun seekBarSetup(){
        runnable = Runnable {
            PlayerActivity.binding.tvSeekBarStart.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.seekBar.progress = mediaPlayer!!.currentPosition.toFloat()
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }
    override fun onAudioFocusChange(focusChange: Int) {
        if(focusChange <= 0){
            //pause music
            PlayerActivity.binding.playPauseBtn.setImageResource(R.drawable.ic_play_pause_song)
            NowPlaying.binding.PlaypauseBtnNP.setImageResource(R.drawable.ic_play_song_icon)
            PlayerActivity.isPlaying = false
            mediaPlayer!!.pause()
            showNotification(R.drawable.ic_play_song_icon)

        }
        else{
            //play music
            PlayerActivity.binding.playPauseBtn.setImageResource(R.drawable.ic_pause_song)
            NowPlaying.binding.PlaypauseBtnNP.setImageResource(R.drawable.ic_pause_song_icon)
            PlayerActivity.isPlaying = true
            mediaPlayer!!.start()
            showNotification(R.drawable.ic_pause_song_icon)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }


}



