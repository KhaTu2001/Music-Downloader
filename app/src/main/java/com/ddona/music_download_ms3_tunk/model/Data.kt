package com.ddona.music_download_ms3_tunk.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity

@Entity(tableName = "music", primaryKeys = ["id","playlist_id"])
data class Data(
    val id: String,
    var playlist_id:Int = -1,
    val albumName: String,
    val artistId: String?,
    val artistName: String,
    val audio: String,
    val duration: Long,
    val albumId: String?,
    val image: String,
    val name: String,
    val audioDownload: String?,
    val status: Int = 0


    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readLong(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt()

        ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeInt(playlist_id)
        parcel.writeString(albumName)
        parcel.writeString(artistId)
        parcel.writeString(artistName)
        parcel.writeString(audio)
        parcel.writeLong(duration)
        parcel.writeString(albumId)
        parcel.writeString(image)
        parcel.writeString(name)
        parcel.writeString(audioDownload)
        parcel.writeInt(status)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Data> {
        override fun createFromParcel(parcel: Parcel): Data {
            return Data(parcel)
        }

        override fun newArray(size: Int): Array<Data?> {
            return arrayOfNulls(size)
        }
    }
}