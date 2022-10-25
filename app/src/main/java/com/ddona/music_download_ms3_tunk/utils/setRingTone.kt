//import android.content.ContentValues
//import android.content.Context
//import android.media.RingtoneManager
//import android.provider.MediaStore
//import com.ddona.music_download_ms3_tunk.model.Data
//
//fun setRingTone(context: Context, data: Data) {
//
//    val values = ContentValues()
//    values.put(MediaStore.MediaColumns.DATA, data.audio.getAbsolutePath())
//    values.put(MediaStore.MediaColumns.TITLE, "Sonify")
//    values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3")
//    values.put(MediaStore.Audio.Media.IS_RINGTONE, true)
//    values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false)
//    values.put(MediaStore.Audio.Media.IS_ALARM, false)
//    values.put(MediaStore.Audio.Media.IS_MUSIC, false)
//
//// Setting ringtone....
//
//// Setting ringtone....
//    getContentResolver().delete(
//        MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
//        MediaStore.Audio.Media.TITLE + " = \"Sonify\"",
//        null
//    )
//// To avoid duplicate inserts
//// To avoid duplicate inserts
//    val ringUri: Uri =
//        getContentResolver().insert(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, values)
//    RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM, ringUri)
//}