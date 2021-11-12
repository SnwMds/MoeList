package com.amanoteam.moelistlibre.data.model.anime

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StartSeason (
    @SerialName("year")
    var year: Int,
    @SerialName("season")
    var season: String
)