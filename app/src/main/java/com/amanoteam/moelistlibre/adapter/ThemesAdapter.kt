package com.amanoteam.moelistlibre.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import com.amanoteam.moelistlibre.adapter.base.BaseAdapter
import com.amanoteam.moelistlibre.data.model.anime.Theme
import com.amanoteam.moelistlibre.databinding.ListItemThemeBinding
import com.amanoteam.moelistlibre.utils.UseCases.copyToClipBoard

class ThemesAdapter(
    private val context: Context
) : BaseAdapter<ListItemThemeBinding, Theme>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ListItemThemeBinding
        get() = ListItemThemeBinding::inflate

    override fun loadData(holder: ViewHolder, position: Int, item: Theme) {
        val themeText = item.text

        holder.binding.theme.text = themeText

        var query = themeText.replace(" ", "+", true)
        if (query.startsWith("#")) {
            query = query.replaceFirst("#", "")
        }

        holder.itemView.setOnClickListener {
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://www.youtube.com/results?search_query=$query")
                context.startActivity(this)
            }
        }

        holder.itemView.setOnLongClickListener {
            themeText.copyToClipBoard(context)
            true
        }
    }

}