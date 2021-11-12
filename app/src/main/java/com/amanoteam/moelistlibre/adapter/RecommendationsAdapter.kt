package com.amanoteam.moelistlibre.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import coil.load
import com.amanoteam.moelistlibre.adapter.base.BasePagingAdapter
import com.amanoteam.moelistlibre.data.model.anime.AnimeList
import com.amanoteam.moelistlibre.databinding.ListItemAnimeBinding

class RecommendationsAdapter(
    private val onClick: (View, AnimeList) -> Unit
) : BasePagingAdapter<ListItemAnimeBinding, AnimeList>(Comparator) {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ListItemAnimeBinding
        get() = ListItemAnimeBinding::inflate

    override fun loadData(holder: ViewHolder, position: Int, item: AnimeList) {

        holder.binding.poster.load(item.node.mainPicture?.medium)

        holder.binding.title.text = item.node.title

        holder.itemView.setOnClickListener { onClick(it, item) }
    }

    object Comparator : DiffUtil.ItemCallback<AnimeList>() {
        override fun areItemsTheSame(oldItem: AnimeList, newItem: AnimeList): Boolean {
            return oldItem.node.id == newItem.node.id
        }

        override fun areContentsTheSame(oldItem: AnimeList, newItem: AnimeList): Boolean {
            return oldItem == newItem
        }
    }
}