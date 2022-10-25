package com.ddona.music_download_ms3_tunk.utils

import android.content.Context
import android.view.LayoutInflater
import com.ddona.music_download_ms3_tunk.R
import com.ddona.music_download_ms3_tunk.databinding.CreatePlaylistSuccessfullyBinding
import com.ddona.music_download_ms3_tunk.ui.fragment.SelectionFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


fun customDialogsuccess(context: Context,text:String) {


    val customDialogsuccess = LayoutInflater.from(context)
        .inflate(R.layout.create_playlist_successfully, SelectionFragment.binding.root, false)
    val build = MaterialAlertDialogBuilder(context)
    val binder = CreatePlaylistSuccessfullyBinding.bind(customDialogsuccess)
    val dialogsuccess = build.setView(customDialogsuccess)
        .create()

    binder.txtSuccess.text = text

    dialogsuccess.show()

    MainScope().launch{
        delay(500)
        dialogsuccess.dismiss()

    }

}