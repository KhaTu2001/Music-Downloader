package com.codingstuff.imageslider

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ddona.music_download_ms3_tunk.callback.CountryItemClick
import com.ddona.music_download_ms3_tunk.databinding.ItemCountryBinding
import com.ddona.music_download_ms3_tunk.model.Country

class CountryAdapter(
    private var countryList: ArrayList<Country>,
    private val callback: CountryItemClick
) :
    RecyclerView.Adapter<CountryAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemCountryBinding) : RecyclerView.ViewHolder(binding.root) {
        val img = binding.flagImg
        val name = binding.countryName
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCountryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.img.setImageResource(countryList[position].url)
        holder.name.text = countryList[position].name
        holder.root.setOnClickListener {
            callback.CountryOnClick(countryList[position])
        }

    }

    fun updateMusicList(Country: ArrayList<Country>) {
        countryList = ArrayList()
        countryList.addAll(Country)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return countryList.size
    }


}