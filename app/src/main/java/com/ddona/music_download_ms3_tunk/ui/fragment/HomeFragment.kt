package com.ddona.music_download_ms3_tunk.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.ddona.music_download_ms3_tunk.adapter.*
import com.ddona.music_download_ms3_tunk.callback.*
import com.ddona.music_download_ms3_tunk.databinding.FragmentHomeBinding
import com.ddona.music_download_ms3_tunk.model.Data
import com.ddona.music_download_ms3_tunk.ui.activity.PlayerActivity
import com.ddona.music_download_ms3_tunk.user_case.UseCases
import com.ddona.music_download_ms3_tunk.viewmodel.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.lang.Runnable
import javax.inject.Inject
import kotlin.math.abs


@AndroidEntryPoint
class HomeFragment : Fragment(), ListenedSongItemClick, GenreItemClick, TrendingSongItemClick,
    DownloadSongItemClick, SliderSongItemClick {

    private lateinit var viewPager2: ViewPager2


    val viewModel: SongViewModel by activityViewModels()

    @Inject
    lateinit var usercase: UseCases

    private lateinit var handler: Handler

    companion object {
        lateinit var binding: FragmentHomeBinding
        var topListSong: ArrayList<Data> = ArrayList()
        var downloadListSong: ArrayList<Data> = ArrayList()
        var trendingListSong: ArrayList<Data> = ArrayList()
        var sliderListSong: ArrayList<Data> = ArrayList()


    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)


        viewModel.isConnected.observe(viewLifecycleOwner) {
            if (it == false) {
                binding.shimmerViewContainer.visibility = View.VISIBLE
                binding.scrollView2.visibility = View.GONE
                binding.shimmerViewContainer.startShimmerAnimation()
                Toast.makeText(
                    requireContext(),
                    "Network unavailable ",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        if (ChangeRegionFragment.id_country != "") {
            binding.countrySelected.visibility = View.VISIBLE

            binding.countryFlag.setImageResource(ChangeRegionFragment.flag_country)
            binding.countryName.text = ChangeRegionFragment.name_country
        }



        sliderVP()

        topTrendingRV()

        topListenedRV()

        topDownRV()

        genreRV()



        binding.countrySelect.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToChangeRegionFragment()
            findNavController(it).navigate(action)
        }
        binding.countrySelected.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToChangeRegionFragment()
            findNavController(it).navigate(action)
        }

        binding.searchSong.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSearchFragment()
            findNavController(it).navigate(action)
        }

        //See All Top Listened

        binding.seeAllTopTrending.setOnClickListener {
            val data = 0
            val action = HomeFragmentDirections.actionHomeFragmentToSeeAllSongFragment(data)
            findNavController(it).navigate(action)
        }

        binding.seeAllTopListend.setOnClickListener {
            val data = 1
            val action = HomeFragmentDirections.actionHomeFragmentToSeeAllSongFragment(data)
            findNavController(it).navigate(action)
        }

        //See All Top Download
        binding.seeAllTopDownload.setOnClickListener {
            val data = 2
            val action = HomeFragmentDirections.actionHomeFragmentToSeeAllSongFragment(data)
            findNavController(it).navigate(action)
        }

        //See All Genre
        binding.seeAllGenre.setOnClickListener {
            val data = 3
            val action = HomeFragmentDirections.actionHomeFragmentToSeeAllSongFragment(data)
            findNavController(it).navigate(action)
        }

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.postDelayed(runnable, 1)
                if (viewPager2.currentItem > 0)
                    handler.removeCallbacks(runnable)

            }
        })

        return binding.root
    }



    private val runnable = Runnable {
        viewPager2.currentItem = viewPager2.currentItem + 1
    }

    private fun sliderVP() {
        viewPager2 = binding.vpSlider
        handler = Handler(Looper.myLooper()!!)

        viewModel.topTrending.observe(viewLifecycleOwner) {
            sliderListSong.addAll(it.take(5))

            viewModel.isConnected.observe(viewLifecycleOwner) {
                if (it == true) {
                    binding.shimmerViewContainer.stopShimmerAnimation()
                    binding.shimmerViewContainer.visibility = View.GONE
                    binding.scrollView2.visibility = View.VISIBLE
                }
            }
        }

        val twadapter = SliderAdapter(sliderListSong, viewPager2, this)
        viewPager2.adapter = twadapter
        viewPager2.offscreenPageLimit = 3
        viewPager2.setCurrentItem(2, true)
        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        setUpTransformer()

    }


    private fun setUpTransformer() {
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.14f
            page.scaleX = 0.85f + r * 0.14f
        }
        viewPager2.setPageTransformer(transformer)
    }

    private fun topTrendingRV() {
        val ttadapter = TopTrendingAdapter(this)
        binding.rvTopTrending.setHasFixedSize(true)
        binding.rvTopTrending.adapter = ttadapter
        viewModel.topTrending.observe(viewLifecycleOwner) {
            ttadapter.submitList(it.take(5))
            trendingListSong.addAll(it)
        }
    }


    private fun genreRV() {
        val gradapter = GenreAdapter(this)
        binding.rvGenre.setHasFixedSize(true)
        binding.rvGenre.adapter = gradapter
        viewModel.listGenre.observe(viewLifecycleOwner) {
            gradapter.submitList(it.take(5))
        }
    }

    private fun topDownRV() {
        val tdadapter = TopDownloadAdapter(this)
        binding.rvTopDownload.setHasFixedSize(true)
        binding.rvTopDownload.adapter = tdadapter
        viewModel.topDownload.observe(viewLifecycleOwner) {
            tdadapter.submitList(it.take(5))
            downloadListSong.addAll(it)

        }

    }

    private fun topListenedRV() {
        val tdadapter = TopListAdapter(requireContext(), this, usercase, false)
        binding.rvTopListend.setHasFixedSize(true)
        binding.rvTopListend.adapter = tdadapter
        viewModel.topListened.observe(viewLifecycleOwner) {
            tdadapter.submitList(it.take(5))
            topListSong.addAll(it)

        }

    }

    override fun downloadOnClick(index: Int) {
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("index", index)
        intent.putExtra("from", "Downloaded")
        startActivity(intent)
    }

    override fun GenreOnClick(keySearch: String, nameGenre: String) {
        val keysearch = keySearch
        val nameGenre = nameGenre
        val action = HomeFragmentDirections.actionHomeFragmentToSeeAllSongByGenreFragment(
            keysearch,
            nameGenre
        )
        findNavController(binding.root).navigate(action)
    }

    override fun ListOnClick(index: Int) {
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("index", index)
        intent.putExtra("from", "Listened")
        startActivity(intent)
    }

    override fun TrendingOnClick(index: Int) {
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("index", index)
        intent.putExtra("from", "Trending")
        startActivity(intent)
    }

    override fun SliderOnClick(index: Int) {
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("index", index)
        intent.putExtra("from", "Trending")
        startActivity(intent)
    }


    override fun onResume() {
        super.onResume()
        

        binding.shimmerViewContainer.startShimmerAnimation()
        if (binding.shimmerViewContainer.visibility == View.GONE)
            binding.scrollView2.visibility = View.VISIBLE


    }


}
