package com.amanoteam.moelistlibre.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import coil.load
import com.amanoteam.moelistlibre.R
import com.amanoteam.moelistlibre.databinding.FragmentFullPosterBinding
import com.amanoteam.moelistlibre.ui.base.BaseFragment
import com.amanoteam.moelistlibre.utils.Extensions.openCustomTab

class FullPosterFragment : BaseFragment<FragmentFullPosterBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentFullPosterBinding
        get() = FragmentFullPosterBinding::inflate

    override fun setup() {
        val imageUrl = arguments?.getString("poster_url")

        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.open_in_browser -> {
                    imageUrl?.let { safeContext.openCustomTab(it) }
                    true
                }
                else -> false
            }
        }

        binding.poster.load(imageUrl) {
            listener(onSuccess = {_, _ -> binding.loading.hide() })
        }
    }
}