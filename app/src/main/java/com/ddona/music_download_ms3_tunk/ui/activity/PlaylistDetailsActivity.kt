package com.ddona.music_download_ms3_tunk.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ddona.music_download_ms3_tunk.R
import com.ddona.music_download_ms3_tunk.adapter.PlaylistAdapter
import com.ddona.music_download_ms3_tunk.adapter.PlaylistDetailsAdapter
import com.ddona.music_download_ms3_tunk.databinding.ActivityPlaylistDetailsBinding
import com.ddona.music_download_ms3_tunk.databinding.CreateNewPlaylistDialogBinding
import com.ddona.music_download_ms3_tunk.databinding.DeletePlaylistDialogBinding
import com.ddona.music_download_ms3_tunk.databinding.RenamePlaylistDialogBinding
import com.ddona.music_download_ms3_tunk.ui.fragment.ChangeRegionFragmentDirections
import com.ddona.music_download_ms3_tunk.ui.fragment.MyMusicFragment
import com.ddona.music_download_ms3_tunk.user_case.UseCases
import com.ddona.music_download_ms3_tunk.user_case.updatePlaylistUserCase
import com.ddona.music_download_ms3_tunk.viewmodel.SongViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PlaylistDetailsActivity : AppCompatActivity() {

    @Inject
    lateinit var usercase: UseCases

    private lateinit var binding: ActivityPlaylistDetailsBinding
    private lateinit var adapter: PlaylistDetailsAdapter
    val viewModel: SongViewModel by viewModels()

    companion object {
        var currentPlaylistPos: Int = -1
        var playlistName: String = ""

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentPlaylistPos = intent.extras?.get("index") as Int
        playlistName = intent.extras?.get("playlistName") as String



        binding.playlistDetailsRV.setHasFixedSize(true)
        binding.playlistNamed.text = playlistName
        binding.playlistDetailsRV.layoutManager = LinearLayoutManager(this)
        adapter = PlaylistDetailsAdapter(this,usercase, currentPlaylistPos)
        binding.playlistDetailsRV.adapter = adapter

        binding.clearFragment.setOnClickListener {
            finish()
        }
        Log.d("fdsfsdfdsf", "showBottomSheetPlaylist: $currentPlaylistPos")




        lifecycleScope.launch {
            viewModel.getAllMusicByPlaylist(currentPlaylistPos).collect {

                binding.txtCountSong.text = it.size.toString() + " Songs"

                if (it.size >= 1) {
                    Glide.with(applicationContext)
                        .load(it[0].image)
                        .apply(
                            RequestOptions().placeholder(R.drawable.ic_new_playlist).centerCrop()
                        )
                        .into(binding.firstSongImg)
                }

                if (it.size >= 2) {
                    Glide.with(applicationContext)
                        .load(it[1].image)
                        .apply(
                            RequestOptions().placeholder(R.drawable.ic_new_playlist).centerCrop()
                        )
                        .into(binding.secondSongImg)
                }
                if (it.size >= 3) {
                    Glide.with(applicationContext)
                        .load(it[2].image)
                        .apply(
                            RequestOptions().placeholder(R.drawable.ic_new_playlist).centerCrop()
                        )
                        .into(binding.threeSongImg)
                }
                if (it.size >= 4) {
                    Glide.with(applicationContext)
                        .load(it[3].image)
                        .apply(
                            RequestOptions().placeholder(R.drawable.ic_new_playlist)
                                .centerCrop()
                        )
                        .into(binding.fourSongImg)
                }

                adapter.submitList(it)

                binding.shimmerViewContainer.stopShimmerAnimation()
                binding.shimmerViewContainer.visibility = View.GONE
                binding.playlistDetailsRV.visibility = View.VISIBLE
            }
        }

        binding.optionPlaylist.setOnClickListener {
            showBottomSheetPlaylist()
        }

    }

    override fun onResume() {
        super.onResume()
        binding.shimmerViewContainer.startShimmerAnimation()
        if (binding.shimmerViewContainer.visibility == View.GONE)
            binding.playlistDetailsRV.visibility = View.VISIBLE
    }

    fun showBottomSheetPlaylist() {
        val bottomSheet: BottomSheetDialog =
            BottomSheetDialog(this)
        bottomSheet.setContentView(R.layout.bottomsheet_playlist_option)

        val reNamePlayList = bottomSheet.findViewById<View>(R.id.ln_rename_playlist)
        val reMovePlayList = bottomSheet.findViewById<View>(R.id.ln_remove_playlist)


        reNamePlayList?.setOnClickListener {
            val customDialog = LayoutInflater.from(this)
                .inflate(R.layout.rename_playlist_dialog, binding.root, false)
            val binder = RenamePlaylistDialogBinding.bind(customDialog)
            val builder = MaterialAlertDialogBuilder(this)
            val dialog = builder.setView(customDialog)
                .create()

            val playlistName = binder.edtNamePlaylist.text
            val submitBtn = binder.acceptActionBtn
            val cancelBtn = binder.cancelActionBtn

            submitBtn.setOnClickListener {
                if (playlistName != null)
                    if (playlistName.isNotEmpty()) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            usercase.updatePlaylistUserCase.invoke(
                                playlistName.toString(),
                                currentPlaylistPos
                            )
                        }

                    }
                binding.playlistNamed.text = playlistName
                Toast.makeText(this, "Rename playlist to $playlistName successfully!!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                bottomSheet.dismiss()

            }

            cancelBtn.setOnClickListener {
                dialog.dismiss()

            }

            dialog.show()
            dialog.setCanceledOnTouchOutside(false);
        }

        reMovePlayList?.setOnClickListener {

            val customDialog = LayoutInflater.from(this)
                .inflate(R.layout.delete_playlist_dialog, binding.root, false)
            val binder = DeletePlaylistDialogBinding.bind(customDialog)
            val builder = MaterialAlertDialogBuilder(this)
            val dialog = builder.setView(customDialog)
                .create()

            val submitBtn = binder.acceptActionBtn
            val cancelBtn = binder.cancelActionBtn

            submitBtn.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    usercase.deletePlaylistUserCase.invoke(currentPlaylistPos)
                    usercase.deleteMusicUserCase.invoke(currentPlaylistPos)
                }
                finish()
                Toast.makeText(this, "Remove playlist successfully!!", Toast.LENGTH_SHORT).show()

            }

            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }


            dialog.show()
            dialog.setCanceledOnTouchOutside(false);
        }


        bottomSheet.show()

    }

}