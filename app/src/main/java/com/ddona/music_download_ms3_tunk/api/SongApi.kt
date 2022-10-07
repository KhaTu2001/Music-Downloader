package com.ddona.music_download_ms3_tunk.api

import com.ddona.music_download_ms3_tunk.model.Genre
import com.ddona.music_download_ms3_tunk.model.Song
import retrofit2.http.GET
import retrofit2.http.Query

interface SongApi {


    @GET("/msd/music/listened")
    suspend fun getAllTopListend() : Song

    @GET("/msd/scmusic/popular")
    suspend fun getAllTopTrending(
        @Query("country")
        countryid:String
    ) : Song

    @GET("/msd/scmusic/download")
    suspend fun getAllTopDownLoad(
        @Query("country")
        countryid:String
    ) : Song

    @GET("/msd/scmusic/genre")
    suspend fun getAllGenres() : Genre

    @GET("/msd/scmusic/bygenre")
    suspend fun getAllSongByGenres(
        @Query("query")
        genreQuery: String,
        @Query("country")
        countryid:String
    ) :Song

    @GET("/msd/music/search")
    suspend fun getSearchMusic(
        @Query("query")
        songName:String
    ) :Song

    @GET("/msd/music/searchgroup")
    suspend fun getSearchAlbum(
        @Query("query")
        AlbumName:String
    ) :Song






}