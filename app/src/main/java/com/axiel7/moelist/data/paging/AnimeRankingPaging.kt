package com.axiel7.moelist.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.axiel7.moelist.data.model.ApiParams
import com.axiel7.moelist.data.model.anime.AnimeRanking
import com.axiel7.moelist.data.network.Api
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

class AnimeRankingPaging(
    private val api: Api,
    private val apiParams: ApiParams,
    private val rankingType: String
) : PagingSource<String, AnimeRanking>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, AnimeRanking> {
        return try {
            val nextPage = params.key
            val response = if (nextPage == null) {
                api.getAnimeRanking(apiParams, rankingType)
            } else {
                api.getAnimeRanking(nextPage)
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

    override fun getRefreshKey(state: PagingState<String, AnimeRanking>): String? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)

            val prevUrl = anchorPage?.prevKey?.toHttpUrlOrNull()
            val prevOffset = prevUrl?.queryParameter("offset")?.toIntOrNull() ?: 0
            //val prevLimit = prevUrl?.queryParameter("limit")?.toIntOrNull() ?: 100
            val prevUrlNew = prevUrl?.newBuilder()
                ?.removeAllQueryParameters("offset")
                ?.addQueryParameter("offset", (prevOffset ).toString())
                ?.build()

            val nextUrl = anchorPage?.nextKey?.toHttpUrlOrNull()
            val nextOffset = nextUrl?.queryParameter("offset")?.toIntOrNull() ?: 100
            //val nextLimit = nextUrl?.queryParameter("limit")?.toIntOrNull() ?: 100
            val nextUrlNew = nextUrl?.newBuilder()
                ?.removeAllQueryParameters("offset")
                ?.addQueryParameter("offset", (nextOffset * 2).toString())
                ?.build()

            prevUrlNew?.toString() ?: nextUrlNew?.toString()
        }
    }
}