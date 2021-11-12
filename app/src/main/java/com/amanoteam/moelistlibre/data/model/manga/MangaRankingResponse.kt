package com.amanoteam.moelistlibre.data.model.manga

import com.amanoteam.moelistlibre.data.model.Paging

data class MangaRankingResponse(
    val data: MutableList<MangaRanking>,
    val paging: Paging
)