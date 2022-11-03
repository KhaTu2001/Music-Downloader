package com.ddona.music_download_ms3_tunk.user_case

data class UseCases(
    val addMusic: addMusicToPlaylistUserCase,
    val countRowSong: CountRowSong,
    val addPlaylist: addPlaylistUserCase,
    val getAllPlaylist: GetAllPlaylistUserCase,
    val getAllPlaylistByName: getAllPlaylistByNameUserCase,
    val getAllMusicByPlaylist: getAllMusicByPlaylistUserCase,
    val updatePlaylistUserCase: updatePlaylistUserCase,
    val deleteMusicFromPlaylistUserCase: deleteMusicFromPlaylistUserCase,
    val deleteMusicUserCase: deleteMusicUserCase,
    val deletePlaylistUserCase: deletePlaylistUserCase,
    val checkName: CheckName,
    val checkSongID: CheckSongID,
    val addMusicToDownloadlistUserCase: addMusicToDownloadlistUserCase,
    val checkID: CheckID,
    val deleteMusicDownloadedUserCase: deleteMusicDownloadedUserCase

    )