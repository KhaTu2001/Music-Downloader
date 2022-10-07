package com.ddona.music_download_ms3_tunk.model

import android.os.Parcel
import android.os.Parcelable


data class Data(
    val albumId: String,
    val albumName: String,
    val artistId: String,
    val artistName: String,
    val audio: String,
    val audioDownload: String,
    val duration: Long,
    val id: String,
    val image: String,
    val name: String,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt().toLong(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }




    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(albumId)
        parcel.writeString(albumName)
        parcel.writeString(artistId)
        parcel.writeString(artistName)
        parcel.writeString(audio)
        parcel.writeString(audioDownload)
        parcel.writeInt(duration.toInt())
        parcel.writeString(id)
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