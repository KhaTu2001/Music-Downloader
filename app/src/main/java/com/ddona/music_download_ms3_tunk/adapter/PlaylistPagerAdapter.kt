package com.ddona.music_download_ms3_tunk.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ddona.music_download_ms3_tunk.ui.fragment.MyMusicFragment
import com.ddona.music_download_ms3_tunk.ui.fragment.OnlineMusicFragment
import java.lang.IllegalArgumentException

class PlaylistPagerAdapter (fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val tabs = arrayOf("MyMusic", "OnlineMusic")
    override fun getItemCount(): Int {
        return tabs.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MyMusicFragment()
            1 -> OnlineMusicFragment()
            else -> throw IllegalArgumentException("Unknow $position")
        }
    }
}