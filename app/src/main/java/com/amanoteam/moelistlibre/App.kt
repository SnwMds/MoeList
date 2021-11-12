package com.amanoteam.moelistlibre

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.util.CoilUtils
import com.amanoteam.moelistlibre.data.network.Api
import com.amanoteam.moelistlibre.data.network.KtorClient
import com.amanoteam.moelistlibre.data.room.AnimeDatabase
import com.amanoteam.moelistlibre.utils.SharedPrefsHelpers
import io.ktor.client.*
import okhttp3.OkHttpClient

class App : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()

        SharedPrefsHelpers.init(applicationContext)
        if (isUserLogged) {
            createKtorClient()
        }

        animeDb = AnimeDatabase.getAnimeDatabase(applicationContext)
    }
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(applicationContext)
            .crossfade(true)
            .crossfade(500)
            .error(R.drawable.ic_launcher_foreground)
            .build()
    }

    companion object {

        fun createKtorClient() {
            ktorClient = KtorClient(accessToken).ktorHttpClient
            api = Api(ktorClient)
        }

        val isUserLogged: Boolean
        get() = SharedPrefsHelpers.instance?.getBoolean("user_logged", false) ?: false

        val accessToken: String
        get() = SharedPrefsHelpers.instance?.getString("access_token", "null") ?: "null"

        val refreshToken: String
        get() = SharedPrefsHelpers.instance?.getString("refresh_token", "null") ?: "null"

        lateinit var animeDb: AnimeDatabase
        private lateinit var ktorClient: HttpClient
        lateinit var api: Api
    }
}