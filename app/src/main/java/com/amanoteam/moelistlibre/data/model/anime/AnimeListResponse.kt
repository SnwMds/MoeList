package com.amanoteam.moelistlibre.data.model.anime

import com.amanoteam.moelistlibre.data.model.Paging

data class AnimeListResponse (
    val data: MutableList<AnimeList>,
    val paging: Paging
)