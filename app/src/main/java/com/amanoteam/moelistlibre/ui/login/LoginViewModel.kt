package com.amanoteam.moelistlibre.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amanoteam.moelistlibre.data.model.AccessToken
import com.amanoteam.moelistlibre.data.network.Api
import com.amanoteam.moelistlibre.data.network.KtorClient
import com.amanoteam.moelistlibre.utils.Constants.MAL_OAUTH2_URL
import com.amanoteam.moelistlibre.utils.PkceGenerator
import io.ktor.client.*
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val ktorClient: HttpClient by lazy {
        KtorClient(null).ktorHttpClient
    }
    private val api: Api by lazy { Api(ktorClient) }

    private val codeVerifier = PkceGenerator.generateVerifier(length = 128)
    val loginUrl = "${MAL_OAUTH2_URL}authorize?response_type=code&client_id=9d64c3963e0f5de53083571d45016565&code_challenge=${codeVerifier}&state=$STATE"

    private val _accessToken = MutableStateFlow<AccessToken?>(null)
    val accessToken: StateFlow<AccessToken?> = _accessToken

    fun getAccessToken(code: String) {
        viewModelScope.launch {
            val call = async { api.getAccessToken(
                clientId = "9d64c3963e0f5de53083571d45016565",
                code = code,
                codeVerifier = codeVerifier,
                grantType = "authorization_code"
            ) }

            val result = try {
                call.await()
            } catch (e: Exception) {
                null
            }

            _accessToken.value = result
        }
    }

    private val _useExternalBrowser = MutableStateFlow(false)
    val useExternalBrowser get() = _useExternalBrowser.value
    fun setUseExternalBrowser(value: Boolean) {
        _useExternalBrowser.value = value
    }

    companion object {
        const val STATE = "MoeList123"
    }
}