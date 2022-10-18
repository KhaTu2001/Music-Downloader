package com.ddona.music_download_ms3_tunk.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.model.playlistMusic
import kotlinx.coroutines.flow.Flow


@Dao
interface MusicDAO {

    @Query("SELECT * FROM music where playlist_id  = :playlistId")
     fun getSongBYPlaylist(playlistId : Int): Flow<List<Data>>

    @Query("SELECT * FROM playlistMusic")
    fun getAllPlaylist(): Flow<List<playlistMusic>>

    @Query("SELECT * FROM playlistMusic Where playlistName LIKE '%' || :playlistName || '%' ")
    fun getAllPlaylistByName(playlistName:String): Flow<List<playlistMusic>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSongtoPlaylist(data: Data)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPlaylist(playlistMusic: playlistMusic)

    @Query("SELECT COUNT(name) FROM music where playlist_id = :playlistId")
    fun getCount(playlistId : Int): Flow<Int>

    @Query("SELECT COUNT(playlistName) FROM playlistMusic where playlistName = :playlistName")
    fun checkName(playlistName : String): Flow<Int>

    @Query("UPDATE playlistMusic set playlistName = :playlistName Where playList_ID = :id")
    fun updatePlayListName(playlistName:String,id:Int)

    @Query("DELETE FROM playlistMusic where playList_ID = :id")
    fun deletePlaylist(id:Int)

    @Query("DELETE FROM music where id = :id and playlist_id = :playlistId")
    fun deleteMusicSongFromPlaylist(id:String,playlistId: Int)

    @Query("DELETE FROM music where playlist_id = :playlistId")
    fun deleteMusicSong(playlistId: Int)

}