package com.ddona.music_download_ms3_tunk.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.model.playlistMusic

@Database(
    entities = [Data::class,playlistMusic::class],
    exportSchema = false,
    version = 1
)
abstract class MusicDatabase :RoomDatabase(){
    abstract  fun musicDao():MusicDAO

        companion object {
            @Volatile
            private var instance: MusicDatabase? = null
            private val LOCK = Any()
            operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
                instance ?: createDatabase(context).also { instance = it }
            }
            private fun createDatabase(context: Context) =
                Room.databaseBuilder(
                    context.applicationContext,
                    MusicDatabase::class.java,
                    "music_db.db"
                ).build()
        }

}