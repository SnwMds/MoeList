package com.amanoteam.moelistlibre.data.model.anime

import com.amanoteam.moelistlibre.data.model.Paging

data class UserAnimeListResponse(
    val data: MutableList<UserAnimeList>,
    val paging: Paging
)