package com.amanoteam.moelistlibre.data.model.manga

import com.amanoteam.moelistlibre.data.model.Paging

data class MangaListResponse (
    val data: MutableList<MangaList>,
    val paging: Paging
)