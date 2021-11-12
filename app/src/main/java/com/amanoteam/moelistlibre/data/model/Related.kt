package com.amanoteam.moelistlibre.data.model

import com.amanoteam.moelistlibre.data.model.anime.AnimeNode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Related(
    @SerialName("node")
    val node: AnimeNode,
    @SerialName("relation_type")
    val relationType: String = "",
    @SerialName("relation_type_formatted")
    val relationTypeFormatted: String = "",
)

fun Related.isManga(): Boolean = node.mediaType == "manga"
        || node.mediaType == "one_shot"
        || node.mediaType == "manhwa"
        || node.mediaType == "novel"
        || node.mediaType == "doujinshi"
        || node.mediaType == "light_novel"
        || node.mediaType == "manhua"