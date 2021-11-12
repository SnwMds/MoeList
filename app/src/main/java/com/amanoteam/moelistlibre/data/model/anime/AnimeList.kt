package com.amanoteam.moelistlibre.data.model.anime

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimeList(
    @SerialName("node")
    val node: AnimeNode
)
