package com.amanoteam.moelistlibre.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import com.amanoteam.moelistlibre.adapter.base.BaseAdapter
import com.amanoteam.moelistlibre.data.model.Related
import com.amanoteam.moelistlibre.databinding.ListItemAnimeRelatedBinding
import com.amanoteam.moelistlibre.utils.StringExtensions.formatRelation

class RelatedsAdapter(
    private val context: Context,
    private val onClick: (View, Related) -> Unit
) : BaseAdapter<ListItemAnimeRelatedBinding, Related>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ListItemAnimeRelatedBinding
        get() = ListItemAnimeRelatedBinding::inflate

    override fun loadData(holder: ViewHolder, position: Int, item: Related) {
        holder.binding.poster.load(item.node.mainPicture?.medium)

        holder.binding.title.text = item.node.title

        holder.binding.relation.text = item.relationTypeFormatted.formatRelation(context)

        holder.itemView.setOnClickListener { onClick(it, item) }
    }
}