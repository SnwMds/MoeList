package com.amanoteam.moelistlibre.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccessToken(
    @SerialName("token_type")
    val tokenType: String = "",
    @SerialName("expires_in")
    val expiresIn: Int = 0,
    @SerialName("access_token")
    val accessToken: String? = null,
    @SerialName("refresh_token")
    val refreshToken: String? = null,

    @SerialName("error")
    val error: String? = null,
    @SerialName("message")
    val message: String? = null,
)