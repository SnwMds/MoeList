package com.amanoteam.moelistlibre.data.model.manga

import com.amanoteam.moelistlibre.data.model.Paging

data class UserMangaListResponse(
    val data: MutableList<UserMangaList>,
    val paging: Paging
)