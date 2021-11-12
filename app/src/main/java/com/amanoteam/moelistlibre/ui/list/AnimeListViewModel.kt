package com.amanoteam.moelistlibre.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.amanoteam.moelistlibre.App
import com.amanoteam.moelistlibre.data.model.ApiParams
import com.amanoteam.moelistlibre.data.model.anime.MyAnimeListStatus
import com.amanoteam.moelistlibre.data.paging.UserAnimeListPaging
import com.amanoteam.moelistlibre.utils.Constants.RESPONSE_ERROR
import com.amanoteam.moelistlibre.utils.Constants.RESPONSE_NONE
import com.amanoteam.moelistlibre.utils.Constants.RESPONSE_OK
import com.amanoteam.moelistlibre.utils.Constants.SORT_ANIME_TITLE
import com.amanoteam.moelistlibre.utils.Constants.SORT_SCORE
import com.amanoteam.moelistlibre.utils.Constants.SORT_UPDATED
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AnimeListViewModel : ViewModel() {

    private val status = MutableStateFlow<String?>(null)
    fun setStatus(value: String) {
        status.value = when (value) {
            "all" -> null
            else -> value
        }
        params.value.status = status.value
    }

    private val sortMode = MutableStateFlow(SORT_ANIME_TITLE)
    fun setSortMode(value: String) {
        sortMode.value = value
        params.value.sort = sortMode.value
    }

    private val nsfw = MutableStateFlow(0)
    fun setNsfw(value: Int) {
        nsfw.value = value
        params.value.nsfw = nsfw.value
    }

    private val params = MutableStateFlow(
        ApiParams(
            status = status.value,
            sort = sortMode.value,
            nsfw = nsfw.value,
            fields = FIELDS
        )
    )

    var animeListFlow = createAnimeListFlow()

    private fun createAnimeListFlow() = Pager(
        PagingConfig(pageSize = 15, prefetchDistance = 10)
    ) {
        UserAnimeListPaging(App.api, params.value)
    }.flow
        .cachedIn(viewModelScope)

    fun updateAnimeListFlow() {
        animeListFlow = createAnimeListFlow()
    }

    private val _updateResponse = MutableStateFlow<Pair<MyAnimeListStatus?, String>>(null to RESPONSE_NONE)
    val updateResponse: StateFlow<Pair<MyAnimeListStatus?, String>> = _updateResponse

    fun updateList(
        animeId: Int,
        status: String? = null,
        score: Int? = null,
        watchedEpisodes: Int? = null
    ) {
        viewModelScope.launch {
            val call = async { App.api.updateUserAnimeList(animeId, status, score, watchedEpisodes) }
            val result = try {
                call.await()
            } catch (e: Exception) {
                null
            }
            if (result != null) {
                _updateResponse.value = result to RESPONSE_OK
            }
            else {
                _updateResponse.value = null to RESPONSE_ERROR
            }

        }
    }

    fun deleteEntry(animeId: Int) {
        viewModelScope.launch {
            val call = async { App.api.deleteAnimeEntry(animeId) }
            call.await()
        }
    }

    fun getPositionFromSort(
        sort: String
    ) : Int = when (sort) {
            SORT_ANIME_TITLE -> 0
            SORT_SCORE -> 1
            SORT_UPDATED -> 2
            else -> 0
    }

    fun getSortFromPosition(
        position: Int
    ) : String = when (position) {
        0 -> SORT_ANIME_TITLE
        1 -> SORT_SCORE
        2 -> SORT_UPDATED
        else -> SORT_ANIME_TITLE
    }

    companion object {
        private const val FIELDS = "list_status,num_episodes,media_type,status"
    }
}