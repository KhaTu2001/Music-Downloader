//import android.content.res.AssetFileDescriptor
//import android.net.Uri
//import android.os.Environment
//import android.util.Log
//import java.io.*
//
//var path = Environment.getExternalStorageDirectory().toString() + "/customsounds"
//
//fun getFile(): File? {
//    val exists = File(path).exists()
//    if (!exists) {
//        File(path).mkdirs()
//    }
//    val newSoundFile = File(path, sound.getFileName().toString() + ".mp3")
//    val mUri = Uri.parse("android.resource://com.example.customsounds/" + sound.getId())
//    val mCr = contentResolver
//    val soundFile: AssetFileDescriptor?
//    soundFile = try {
//        mCr.openAssetFileDescriptor(mUri, "r")
//    } catch (e: FileNotFoundException) {
//        null
//    }
//    try {
//        val readData = ByteArray(1024)
//        val fis: FileInputStream = soundFile!!.createInputStream()
//        val fos = FileOutputStream(newSoundFile)
//        var i: Int = fis.read(readData)
//        while (i != -1) {
//            fos.write(readData, 0, i)
//            i = fis.read(readData)
//        }
//        fos.close()
//    } catch (io: IOException) {
//        Log.e("sfdsd", "io exception")
//        return null
//    }
//    return newSoundFile
//}