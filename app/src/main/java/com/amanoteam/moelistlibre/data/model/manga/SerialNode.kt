package com.amanoteam.moelistlibre.data.model.manga

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SerialNode(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String
)