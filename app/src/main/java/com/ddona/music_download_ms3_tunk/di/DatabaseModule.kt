package com.ddona.music_download_ms3_tunk.di

import android.app.Application
import androidx.room.Room
import com.ddona.music_download_ms3_tunk.db.MusicDAO
import com.ddona.music_download_ms3_tunk.db.MusicDatabase
import com.ddona.music_download_ms3_tunk.reponsitory.MusicRepository
import com.ddona.music_download_ms3_tunk.user_case.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun providePlaylistDatabase(app: Application): MusicDatabase {
        return Room.databaseBuilder(
            app.applicationContext,
            MusicDatabase::class.java,
            "music.db"
        ).build()
    }

    @Provides
    @Singleton
    fun providePlaylistDao(db: MusicDatabase): MusicDAO {
        return db.musicDao()
    }

    @Provides
    @Singleton
    fun provideMusicRepository(db: MusicDatabase): MusicRepository {
        return MusicRepositoryImpl(db.musicDao())
    }

    @Provides
    @Singleton
    fun provideMusicUserCases(repo: MusicRepository): UseCases {
        return UseCases(
            addMusic = addMusicToPlaylistUserCase(repo),
            addPlaylist = addPlaylistUserCase(repo),
            countRowSong = CountRowSong(repo),
            getAllPlaylist = GetAllPlaylistUserCase(repo),
            getAllPlaylistByName = getAllPlaylistByNameUserCase(repo),
            getAllMusicByPlaylist = getAllMusicByPlaylistUserCase(repo),
            updatePlaylistUserCase = updatePlaylistUserCase(repo),
            deleteMusicFromPlaylistUserCase = deleteMusicFromPlaylistUserCase(repo),
            deleteMusicUserCase = deleteMusicUserCase(repo),
            deletePlaylistUserCase = deletePlaylistUserCase(repo),
            checkName = CheckName(repo),
            checkSongID = CheckSongID(repo),
            addMusicToDownloadlistUserCase = addMusicToDownloadlistUserCase(repo),
            checkID = CheckID(repo),
            deleteMusicDownloadedUserCase = deleteMusicDownloadedUserCase(repo)
            )
    }
}