package com.amanoteam.moelistlibre.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.amanoteam.moelistlibre.data.model.ApiParams
import com.amanoteam.moelistlibre.data.model.anime.AnimeList
import com.amanoteam.moelistlibre.data.network.Api

class AnimeListPaging(
    private val api: Api,
    private val apiParams: ApiParams
) : PagingSource<String, AnimeList>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, AnimeList> {
        return try {
            val nextPage = params.key
            val response = if (nextPage == null) {
                api.getAnimeList(apiParams)
            } else {
                api.getAnimeList(nextPage)
            }
            LoadResult.Page(
                data = response.data!!,
                prevKey = null,
                nextKey = response.paging?.next
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, AnimeList>): String? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)

            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
    }
}