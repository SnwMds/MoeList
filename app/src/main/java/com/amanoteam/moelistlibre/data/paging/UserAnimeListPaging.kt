package com.amanoteam.moelistlibre.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.amanoteam.moelistlibre.data.model.ApiParams
import com.amanoteam.moelistlibre.data.model.anime.UserAnimeList
import com.amanoteam.moelistlibre.data.network.Api

class UserAnimeListPaging(
    private val api: Api,
    private val apiParams: ApiParams
) : PagingSource<String, UserAnimeList>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, UserAnimeList> {
        return try {
            val nextPage = params.key
            val response = if (nextPage==null) {
                api.getUserAnimeList(apiParams)
            } else {
                api.getUserAnimeList(nextPage)
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

    override fun getRefreshKey(state: PagingState<String, UserAnimeList>): String? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)

            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
    }
}