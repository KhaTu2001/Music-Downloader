package com.ddona.music_download_ms3_tunk.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ddona.music_download_ms3_tunk.ui.fragment.DownloadedFragment
import com.ddona.music_download_ms3_tunk.ui.fragment.DownloandingFragment
import java.lang.IllegalArgumentException

class DownloadPagerAdapter (fragment: Fragment,val index:Int) : FragmentStateAdapter(fragment) {
    private val tabs = arrayOf("Downloaded", "Downloading")
    override fun getItemCount(): Int {
        return tabs.size
    }

    override fun createFragment(position: Int): Fragment {
            if (index == 0) {
                return when (position) {

                    0 -> DownloadedFragment()
                    1 -> DownloandingFragment()
                    else -> throw IllegalArgumentException("Unknow $position")

                }
            } else {
                return when (position) {

                    0 -> DownloandingFragment()
                    1 -> DownloadedFragment()
                    else -> throw IllegalArgumentException("Unknow $position")

                }
            }

    }
}