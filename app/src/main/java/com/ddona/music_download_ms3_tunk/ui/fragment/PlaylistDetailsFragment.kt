package com.ddona.music_download_ms3_tunk.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ddona.music_download_ms3_tunk.R
import com.ddona.music_download_ms3_tunk.adapter.PlaylistDetailsAdapter
import com.ddona.music_download_ms3_tunk.databinding.*
import com.ddona.music_download_ms3_tunk.user_case.UseCases
import com.ddona.music_download_ms3_tunk.viewmodel.SongViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class PlaylistDetailsFragment : Fragment() {


    @Inject
    lateinit var usercase: UseCases

    val args: PlaylistDetailsFragmentArgs by navArgs()

    private lateinit var binding: FragmentPlaylistDetailsBinding
    private lateinit var adapter: PlaylistDetailsAdapter
    val viewModel: SongViewModel by activityViewModels()

    var currentPlaylistPos: Int = -1
    var status: Int = -1

    var playlistName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPlaylistDetailsBinding.inflate(inflater)

        currentPlaylistPos = args.currentPlaylist
        playlistName = args.playlistName
        status = args.status



        binding.btnAddSong.setOnClickListener {
            val action =
                PlaylistDetailsFragmentDirections.actionPlaylistDetailsFragmentToSelectionFragment(
                    currentPlaylistPos, 1,status
                )
            findNavController().navigate(action)
        }

        binding.playlistDetailsRV.setHasFixedSize(true)
        binding.playlistNamed.text = playlistName
        binding.playlistDetailsRV.layoutManager = LinearLayoutManager(requireContext())

        adapter = PlaylistDetailsAdapter(requireContext(), usercase, currentPlaylistPos)
        binding.playlistDetailsRV.adapter = adapter

        viewModel.isConnected.observe(viewLifecycleOwner) {
            if (it == false) {
                lifecycleScope.launch {
                    viewModel.getAllMusicByPlaylistOff(currentPlaylistPos).collect {

                        binding.txtCountSong.text = it.size.toString() + " Songs"

                        if (it.size >= 1) {
                            Glide.with(requireContext())
                                .load(it[0].image)
                                .apply(
                                    RequestOptions().placeholder(R.drawable.ic_new_playlist).centerCrop()
                                )
                                .into(binding.firstSongImg)
                        }

                        if (it.size >= 2) {
                            Glide.with(requireContext())
                                .load(it[1].image)
                                .apply(
                                    RequestOptions().placeholder(R.drawable.ic_new_playlist).centerCrop()
                                )
                                .into(binding.secondSongImg)
                        }

                        if (it.size >= 3) {
                            Glide.with(requireContext())
                                .load(it[2].image)
                                .apply(
                                    RequestOptions().placeholder(R.drawable.ic_new_playlist).centerCrop()
                                )
                                .into(binding.threeSongImg)
                        }
                        if (it.size >= 4) {
                            Glide.with(requireContext())
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

            }
            else{
                lifecycleScope.launch {
                    viewModel.getAllMusicByPlaylist(currentPlaylistPos).collect {

                        binding.txtCountSong.text = it.size.toString() + " Songs"

                        if (it.size >= 1) {
                            Glide.with(requireContext())
                                .load(it[0].image)
                                .apply(
                                    RequestOptions().placeholder(R.drawable.ic_new_playlist).centerCrop()
                                )
                                .into(binding.firstSongImg)
                        }

                        if (it.size >= 2) {
                            Glide.with(requireContext())
                                .load(it[1].image)
                                .apply(
                                    RequestOptions().placeholder(R.drawable.ic_new_playlist).centerCrop()
                                )
                                .into(binding.secondSongImg)
                        }

                        if (it.size >= 3) {
                            Glide.with(requireContext())
                                .load(it[2].image)
                                .apply(
                                    RequestOptions().placeholder(R.drawable.ic_new_playlist).centerCrop()
                                )
                                .into(binding.threeSongImg)
                        }
                        if (it.size >= 4) {
                            Glide.with(requireContext())
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
            }
        }



        binding.optionPlaylist.setOnClickListener {
            showBottomSheetPlaylist()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.shimmerViewContainer.startShimmerAnimation()
        if (binding.shimmerViewContainer.visibility == View.GONE)
            binding.playlistDetailsRV.visibility = View.VISIBLE
        binding.root.hideKeyboard()
    }

    fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    fun showBottomSheetPlaylist() {
        val bottomSheet: BottomSheetDialog =
            BottomSheetDialog(requireContext())
        bottomSheet.setContentView(R.layout.bottomsheet_playlist_option)

        val reNamePlayList = bottomSheet.findViewById<View>(R.id.ln_rename_playlist)
        val reMovePlayList = bottomSheet.findViewById<View>(R.id.ln_remove_playlist)


        reNamePlayList?.setOnClickListener {
            val customDialog = LayoutInflater.from(context)
                .inflate(R.layout.rename_playlist_dialog, binding.root, false)
            val binder = RenamePlaylistDialogBinding.bind(customDialog)
            val builder = MaterialAlertDialogBuilder(requireContext())
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
                Toast.makeText(
                    context,
                    "Rename playlist to $playlistName successfully!!",
                    Toast.LENGTH_SHORT
                ).show()
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

            val customDialog = LayoutInflater.from(context)
                .inflate(R.layout.delete_playlist_dialog, binding.root, false)
            val binder = DeletePlaylistDialogBinding.bind(customDialog)
            val builder = MaterialAlertDialogBuilder(requireContext())
            val dialog = builder.setView(customDialog)
                .create()

            val submitBtn = binder.acceptActionBtn
            val cancelBtn = binder.cancelActionBtn

            submitBtn.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    usercase.deletePlaylistUserCase.invoke(currentPlaylistPos)
                    usercase.deleteMusicUserCase.invoke(currentPlaylistPos)
                }
                Toast.makeText(context, "Remove playlist successfully!!", Toast.LENGTH_SHORT).show()

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