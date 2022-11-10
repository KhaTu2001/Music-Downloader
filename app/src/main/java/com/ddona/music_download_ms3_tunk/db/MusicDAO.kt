package com.ddona.music_download_ms3_tunk.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.model.playlistMusic
import com.ddona.music_download_ms3_tunk.model.songDownloaded
import kotlinx.coroutines.flow.Flow


@Dao
interface MusicDAO {

    @Query("SELECT * FROM music where playlist_id  = :playlistId")
     fun getSongBYPlaylist(playlistId : Int): Flow<List<Data>>

    @Query("SELECT * FROM music where playlist_id  = :playlistId and status = 1")
    fun getSongBYPlaylistOff(playlistId : Int): Flow<List<Data>>

    @Query("SELECT * FROM playlistMusic where status = :status")
    fun getAllPlaylist(status:Int): Flow<List<playlistMusic>>

    @Query("SELECT * FROM playlistMusic")
    fun getAllPlaylistNoStatus(): Flow<List<playlistMusic>>

    @Query("SELECT * FROM playlistMusic Where playlistName LIKE '%' || :playlistName || '%' and status = :status")
    fun getAllPlaylistByName(playlistName:String,status: Int): Flow<List<playlistMusic>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSongtoPlaylist(data: Data)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSongtoDownloadlist(songDownloaded: songDownloaded )

    @Query("SELECT COUNT(id) FROM songDownloaded where id = :id")
    fun checkid(id : String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPlaylist(playlistMusic: playlistMusic)

    @Query("SELECT COUNT(name) FROM music where playlist_id = :playlistId")
    fun getCount(playlistId : Int): Flow<Int>

    @Query("SELECT COUNT(playlistName) FROM playlistMusic where playlistName = :playlistName")
    fun checkName(playlistName : String): Flow<Int>

    @Query("SELECT COUNT(id) FROM music where playlist_id = :playlistID and id = :id")
    fun checkSongName(playlistID : Int,id: String): Flow<Int>

    @Query("UPDATE playlistMusic set playlistName = :playlistName Where playList_ID = :id")
    fun updatePlayListName(playlistName:String,id:Int)

    @Query("DELETE FROM playlistMusic where playList_ID = :id")
    fun deletePlaylist(id:Int)

    @Query("DELETE FROM music where id = :id and playlist_id = :playlistId")
    fun deleteMusicSongFromPlaylist(id:String,playlistId: Int)

    @Query("DELETE FROM music where playlist_id = :playlistId")
    fun deleteMusicSong(playlistId: Int)

    @Query("DELETE FROM songDownloaded where name = :id")
    fun deleteMusicDonwloaded(id: String)

}