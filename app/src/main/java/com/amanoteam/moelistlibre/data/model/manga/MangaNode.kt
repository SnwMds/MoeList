package com.amanoteam.moelistlibre.data.model.manga

import com.amanoteam.moelistlibre.data.model.MainPicture
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MangaNode(
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("main_picture")
    val mainPicture: MainPicture? = null,
    @SerialName("start_date")
    val startDate: String? = null,
    @SerialName("num_volumes")
    val numVolumes: Int? = null,
    @SerialName("num_chapters")
    val numChapters: Int? = null,
    @SerialName("num_list_users")
    val numListUsers: Int? = null,
    @SerialName("media_type")
    val mediaType: String? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("mean")
    val mean: Float? = null
)