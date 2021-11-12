package com.amanoteam.moelistlibre.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MainPicture(
    @SerialName("medium")
    val medium: String,
    @SerialName("large")
    val large: String
)