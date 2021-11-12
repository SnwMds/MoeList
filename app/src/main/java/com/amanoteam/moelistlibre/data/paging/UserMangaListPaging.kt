package com.amanoteam.moelistlibre.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.amanoteam.moelistlibre.data.model.ApiParams
import com.amanoteam.moelistlibre.data.model.manga.UserMangaList
import com.amanoteam.moelistlibre.data.network.Api

class UserMangaListPaging(
    private val api: Api,
    private val apiParams: ApiParams
) : PagingSource<String, UserMangaList>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, UserMangaList> {
        return try {
            val nextPage = params.key
            val response = if (nextPage==null) {
                api.getUserMangaList(apiParams)
            } else {
                api.getUserMangaList(nextPage)
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

    override fun getRefreshKey(state: PagingState<String, UserMangaList>): String? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)

            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
    }
}