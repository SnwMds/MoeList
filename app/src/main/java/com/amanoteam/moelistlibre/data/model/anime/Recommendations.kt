package com.amanoteam.moelistlibre.data.model.anime

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Recommendations(
    @SerialName("node")
    val node: AnimeNode,
    @SerialName("num_recommendations")
    val numRecommendations: Int
)