package com.amanoteam.moelistlibre.data.model.anime

import com.amanoteam.moelistlibre.data.model.Paging

data class SeasonalAnimeResponse(
    val data: MutableList<AnimeSeasonal>,
    val paging: Paging?,
    val season: StartSeason?
)