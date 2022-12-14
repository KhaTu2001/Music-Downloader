package com.ddona.music_download_ms3_tunk.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.codingstuff.imageslider.CountryAdapter
import com.ddona.music_download_ms3_tunk.R
import com.ddona.music_download_ms3_tunk.callback.CountryItemClick
import com.ddona.music_download_ms3_tunk.databinding.FragmentChangeRegionBinding
import com.ddona.music_download_ms3_tunk.model.Country
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ChangeRegionFragment : Fragment(), CountryItemClick {

    private lateinit var binding: FragmentChangeRegionBinding
    private lateinit var adapter: CountryAdapter
    var regionListSearch: ArrayList<Country> = ArrayList()
    var countryList: ArrayList<Country> = ArrayList()


    companion object {
        var id_country: String = ""
        var name_country: String = ""
        var flag_country: Int = 0

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChangeRegionBinding.inflate(inflater)
        init()
        binding.rvCountry.setHasFixedSize(true)
        adapter = CountryAdapter(countryList, this)
        binding.rvCountry.adapter = adapter


        var job: Job? = null
        binding.edtSearch.addTextChangedListener { editable ->
            job?.cancel()
            regionListSearch = ArrayList()
            job = MainScope().launch {
                editable?.let {
                    for (country in countryList)
                        if (country.name.lowercase().contains(editable)) {
                            regionListSearch.add(country)
                        }
                    adapter.updateMusicList(regionListSearch)
                }
            }
        }
        return binding.root
    }

    private fun init() {
        countryList.add(Country("us", "US", R.drawable.ic_united_states))
        countryList.add(Country("gb", "UK", R.drawable.ic_united_kingdom))
        countryList.add(Country("au", "Australia", R.drawable.ic_australia))
        countryList.add(Country("kr", "Korea", R.drawable.ic_south_korea))
        countryList.add(Country("de", "Germany", R.drawable.ic_germany))
        countryList.add(Country("jp", "Japan", R.drawable.ic_japan))
        countryList.add(Country("vn", "Vi???t Nam", R.drawable.ic_vietnam))
        countryList.add(Country("br", "Brazil", R.drawable.ic_brazil))
        countryList.add(Country("fr", "France", R.drawable.ic_france))

    }

    override fun CountryOnClick(country: Country) {
        id_country = country.id
        name_country = country.name
        flag_country = country.url


        val action = ChangeRegionFragmentDirections.actionChangeRegionFragmentToHomeFragment()
        findNavController().navigate(action)
    }


}

