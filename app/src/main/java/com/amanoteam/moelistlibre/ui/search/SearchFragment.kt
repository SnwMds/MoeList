package com.amanoteam.moelistlibre.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import com.amanoteam.moelistlibre.R
import com.amanoteam.moelistlibre.adapter.SearchAnimeAdapter
import com.amanoteam.moelistlibre.adapter.SearchMangaAdapter
import com.amanoteam.moelistlibre.databinding.FragmentSearchBinding
import com.amanoteam.moelistlibre.ui.base.BaseFragment
import com.amanoteam.moelistlibre.ui.main.MainViewModel
import com.amanoteam.moelistlibre.utils.Extensions.toInt
import kotlinx.coroutines.flow.collectLatest

class SearchFragment : BaseFragment<FragmentSearchBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSearchBinding
        get() = FragmentSearchBinding::inflate
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var searchAnimeAdapter: SearchAnimeAdapter
    private lateinit var searchMangaAdapter: SearchMangaAdapter
    private val searchType: Int by lazy {
        arguments?.getInt("type") ?: 0
    }

    override fun onResume() {
        super.onResume()
        binding.loading.hide()
    }

    override fun setup() {
        viewModel.setNsfw(sharedPref.getBoolean("nsfw", false).toInt())
        when (searchType) {
            0 -> initAnimeSearch()
            1 -> initMangaSearch()
        }

    }

    private fun initAnimeSearch() {
        searchAnimeAdapter = SearchAnimeAdapter(safeContext,
            onClick = { _, item, _ ->
                mainViewModel.selectId(item.node.id)
                mainActivity?.navigate(
                    R.id.action_hostSearchFragment_to_animeDetailsFragment,
                )
            }
        )
        binding.listSearch.adapter = searchAnimeAdapter

        launchLifecycleStarted {
            searchAnimeAdapter.loadStateFlow.collectLatest {
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
            viewModel.query.collectLatest { q ->
                if (q.isNotBlank()) {
                    launchLifecycleStarted {
                        viewModel.animeListFlow.collectLatest {
                            searchAnimeAdapter.submitData(it)
                        }
                    }
                }
            }
        }
    }

    private fun initMangaSearch() {
        searchMangaAdapter = SearchMangaAdapter(
            safeContext,
            onClick = { _, item, _ ->
                mainViewModel.selectId(item.node.id)
                mainActivity?.navigate(
                    R.id.action_hostSearchFragment_to_mangaDetailsFragment,
                )
            }
        )
        binding.listSearch.adapter = searchMangaAdapter

        launchLifecycleStarted {
            searchMangaAdapter.loadStateFlow.collectLatest {
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
            viewModel.query.collectLatest { q ->
                if (q.isNotBlank()) {
                    launchLifecycleStarted {
                        viewModel.mangaListFlow.collectLatest {
                            searchMangaAdapter.submitData(it)
                        }
                    }
                }
            }
        }
    }

    fun search(query: String) {
        if (query.isNotBlank() && query != viewModel.query.value) {
            viewModel.setQuery(query)
            when (searchType) {
                0 -> searchAnimeAdapter.refresh()
                1 -> searchMangaAdapter.refresh()
            }
        }
    }

}