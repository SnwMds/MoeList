package com.amanoteam.moelistlibre.ui.ranking

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import com.amanoteam.moelistlibre.R
import com.amanoteam.moelistlibre.adapter.RankingAnimeAdapter
import com.amanoteam.moelistlibre.adapter.RankingMangaAdapter
import com.amanoteam.moelistlibre.databinding.FragmentRankingBinding
import com.amanoteam.moelistlibre.ui.base.BaseFragment
import com.amanoteam.moelistlibre.ui.main.MainViewModel
import com.amanoteam.moelistlibre.utils.Extensions.toInt
import kotlinx.coroutines.flow.collectLatest

class RankingFragment : BaseFragment<FragmentRankingBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRankingBinding
        get() = FragmentRankingBinding::inflate
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: RankingViewModel by viewModels()
    private lateinit var adapterAnime: RankingAnimeAdapter
    private lateinit var adapterManga: RankingMangaAdapter
    private val type: Int by lazy {
        arguments?.getInt("type", 0) ?: 0
    }

    override fun setup() {
        viewModel.setNsfw(sharedPref.getBoolean("nsfw", false).toInt())
        viewModel.setRankType(arguments?.getString("rank_type", "all") ?: "all")

        when (type) {
            0 -> initAnimeRanking()
            1 -> initMangaRanking()
        }
    }

    private fun initAnimeRanking() {
        adapterAnime = RankingAnimeAdapter(safeContext,
            onClick = { itemView, item ->
                mainViewModel.selectId(item.node.id)
                mainActivity?.navigate(
                    idAction = R.id.action_animeRankingFragment_to_animeDetailsFragment
                )
            }
        )
        binding.listRanking.adapter = adapterAnime

        launchLifecycleStarted {
            adapterAnime.loadStateFlow.collectLatest {
                if (it.refresh is LoadState.Loading) {
                    binding.loading.show()
                } else {
                    binding.loading.hide()
                }
                if (it.refresh is LoadState.Error) {
                    showSnackbar(getString(R.string.error_server))
                }
            }
        }

        launchLifecycleStarted {
            viewModel.animeRankingFlow.collectLatest {
                adapterAnime.submitData(it)
            }
        }
    }

    private fun initMangaRanking() {
        adapterManga = RankingMangaAdapter(safeContext,
            onClick = { itemView, item ->
                mainViewModel.selectId(item.node.id)
                mainActivity?.navigate(
                    idAction = R.id.action_mangaRankingFragment_to_mangaDetailsFragment
                )
            }
        )
        binding.listRanking.adapter = adapterManga

        launchLifecycleStarted {
            adapterManga.loadStateFlow.collectLatest {
                if (it.refresh is LoadState.Loading) {
                    binding.loading.show()
                } else {
                    binding.loading.hide()
                }
                if (it.refresh is LoadState.Error) {
                    showSnackbar(getString(R.string.error_server))
                }
            }
        }

        launchLifecycleStarted {
            viewModel.mangaRankingFlow.collectLatest {
                adapterManga.submitData(it)
            }
        }
    }

}