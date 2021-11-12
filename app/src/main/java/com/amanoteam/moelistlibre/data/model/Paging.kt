package com.amanoteam.moelistlibre.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Paging (
    @SerialName("next")
    val next: String? = null,
    @SerialName("previous")
    val previous: String? = null
)