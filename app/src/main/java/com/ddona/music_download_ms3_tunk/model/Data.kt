package com.ddona.music_download_ms3_tunk.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "music", primaryKeys = ["id","playlist_id"])
data class Data(
    val id: String,
    val playlist_id:Int = -1,
    val albumName: String,
    val artistId: String,
    val artistName: String,
    val audio: String,
    val audioDownload: String,
    val duration: Long,
    val albumId: String,
    val image: String,
    val name: String,

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readLong(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeInt(playlist_id)
        parcel.writeString(albumName)
        parcel.writeString(artistId)
        parcel.writeString(artistName)
        parcel.writeString(audio)
        parcel.writeString(audioDownload)
        parcel.writeLong(duration)
        parcel.writeString(albumId)
        parcel.writeString(image)
        parcel.writeString(name)
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