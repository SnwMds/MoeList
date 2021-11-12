package com.amanoteam.moelistlibre.data.model.anime

import com.amanoteam.moelistlibre.data.model.Paging

data class AnimeRankingResponse(
    val data: MutableList<AnimeRanking>,
    val paging: Paging
)

