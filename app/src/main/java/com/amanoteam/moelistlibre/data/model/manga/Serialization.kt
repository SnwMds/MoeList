package com.amanoteam.moelistlibre.data.model.manga

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Serialization(
    @SerialName("node")
    val node: SerialNode
)