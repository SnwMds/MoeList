package com.amanoteam.moelistlibre.data.network

import com.amanoteam.moelistlibre.data.model.AccessToken
import com.amanoteam.moelistlibre.data.model.ApiParams
import com.amanoteam.moelistlibre.data.model.Response
import com.amanoteam.moelistlibre.data.model.User
import com.amanoteam.moelistlibre.data.model.anime.*
import com.amanoteam.moelistlibre.data.model.manga.*
import com.amanoteam.moelistlibre.utils.Constants.MAL_API_URL
import com.amanoteam.moelistlibre.utils.Constants.MAL_OAUTH2_URL
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

class Api(private val client: HttpClient) {

    // Login

    suspend fun getAccessToken(
        clientId: String,
        code: String,
        codeVerifier: String,
        grantType: String
    ): AccessToken = client.post("${MAL_OAUTH2_URL}token") {
        body = FormDataContent(Parameters.build {
            append("client_id", clientId)
            append("code", code)
            append("code_verifier", codeVerifier)
            append("grant_type", grantType)
        })
    }

    suspend fun getAccessToken(
        clientId: String,
        refreshToken: String,
        grantType: String
    ): AccessToken = client.post("${MAL_OAUTH2_URL}token") {
        body = FormDataContent(Parameters.build {
            append("client_id", clientId)
            append("refresh_token", refreshToken)
            append("grant_type", grantType)
        })
    }

    // Anime

    suspend fun getAnimeList(
        params: ApiParams
    ): Response<List<AnimeList>> = client.get("${MAL_API_URL}anime") {
        parameter("q", params.q)
        parameter("limit", params.limit)
        parameter("offset", params.offset)
        parameter("nsfw", params.nsfw)
        parameter("fields", params.fields)
    }

    suspend fun getAnimeList(url: String) : Response<List<AnimeList>> = client.get(url)

    suspend fun getSeasonalAnime(
        params: ApiParams,
        year: Int,
        season: String
    ): Response<List<AnimeSeasonal>> = client.get("${MAL_API_URL}anime/season/$year/$season") {
        parameter("sort", params.sort)
        parameter("nsfw", params.nsfw)
        parameter("fields", params.fields)
        parameter("limit", params.limit)
    }

    suspend fun getSeasonalAnime(url: String) : Response<List<AnimeSeasonal>> = client.get(url)

    suspend fun getAnimeRanking(
        params: ApiParams,
        rankingType: String
    ): Response<List<AnimeRanking>> = client.get("${MAL_API_URL}anime/ranking") {
        parameter("ranking_type", rankingType)
        parameter("nsfw", params.nsfw)
        parameter("fields", params.fields)
        parameter("limit", params.limit)
    }

    suspend fun getAnimeRanking(url: String) : Response<List<AnimeRanking>> = client.get(url)

    suspend fun getAnimeRecommendations(
        params: ApiParams = ApiParams()
    ): Response<List<AnimeList>> = client.get("${MAL_API_URL}anime/suggestions") {
        parameter("nsfw", params.nsfw)
        parameter("fields", params.fields)
        parameter("limit", params.limit)
    }

    suspend fun getAnimeRecommendations(url: String) : Response<List<AnimeList>> = client.get(url)

    suspend fun getUserAnimeList(
        params: ApiParams
    ): Response<List<UserAnimeList>> = client.get("${MAL_API_URL}users/@me/animelist") {
        parameter("status", params.status)
        parameter("sort", params.sort)
        parameter("nsfw", params.nsfw)
        parameter("fields", params.fields)
    }

    suspend fun getUserAnimeList(url: String) : Response<List<UserAnimeList>> = client.get(url)

    //TODO (implement: is_rewatching, priority, num_times_rewatched, rewatch_value, tags, comments)
    suspend fun updateUserAnimeList(
        animeId: Int,
        status: String?,
        score: Int?,
        watchedEpisodes: Int?,
    ): MyAnimeListStatus = client.request("${MAL_API_URL}anime/$animeId/my_list_status") {
        method = HttpMethod.Patch
        body = FormDataContent(Parameters.build {
            if (status != null) append("status", status)
            if (score != null) append("score", score.toString())
            if (watchedEpisodes != null) append("num_watched_episodes", watchedEpisodes.toString())
        })
    }

    suspend fun deleteAnimeEntry(
        animeId: Int
    ): Unit = client.delete("${MAL_API_URL}anime/$animeId/my_list_status")

    suspend fun getAnimeDetails(
        animeId: Int,
        fields: String?
    ): AnimeDetails = client.get("${MAL_API_URL}anime/$animeId") {
        parameter("fields", fields)
    }

    // Manga

    suspend fun getMangaList(
        params: ApiParams
    ): Response<List<MangaList>> = client.get("${MAL_API_URL}manga") {
        parameter("q", params.q)
        parameter("limit", params.limit)
        parameter("offset", params.offset)
        parameter("nsfw", params.nsfw)
        parameter("fields", params.fields)
    }

    suspend fun getMangaList(url: String) : Response<List<MangaList>> = client.get(url)

    suspend fun getUserMangaList(
        params: ApiParams
    ): Response<List<UserMangaList>> = client.get("${MAL_API_URL}users/@me/mangalist") {
        parameter("status", params.status)
        parameter("sort", params.sort)
        parameter("nsfw", params.nsfw)
        parameter("fields", params.fields)
    }

    suspend fun getMangaRanking(
        params: ApiParams,
        rankingType: String
    ): Response<List<MangaRanking>> = client.get("${MAL_API_URL}manga/ranking") {
        parameter("ranking_type", rankingType)
        parameter("nsfw", params.nsfw)
        parameter("fields", params.fields)
        parameter("limit", params.limit)
    }

    suspend fun getMangaRanking(url: String) : Response<List<MangaRanking>> = client.get(url)

    suspend fun getUserMangaList(url: String) : Response<List<UserMangaList>> = client.get(url)

    //TODO (implement: is_rereading, priority, num_times_reread, reread_value, tags, comments)
    suspend fun updateUserMangaList(
        mangaId: Int,
        status: String?,
        score: Int?,
        chaptersRead: Int?,
        volumesRead: Int?
    ): MyMangaListStatus = client.request("${MAL_API_URL}manga/$mangaId/my_list_status") {
        method = HttpMethod.Patch
        body = FormDataContent(Parameters.build {
            if (status != null) append("status", status)
            if (score != null) append("score", score.toString())
            if (chaptersRead != null) append("num_chapters_read", chaptersRead.toString())
            if (volumesRead != null) append("num_volumes_read", volumesRead.toString())
        })
    }

    suspend fun deleteMangaEntry(
        mangaId: Int
    ): Unit = client.delete("${MAL_API_URL}manga/$mangaId/my_list_status")

    suspend fun getMangaDetails(
        mangaId: Int,
        fields: String?
    ): MangaDetails = client.get("${MAL_API_URL}manga/$mangaId") {
        parameter("fields", fields)
    }

    // User

    suspend fun getUser(
        fields: String?
    ): User = client.get("${MAL_API_URL}users/@me") {
        parameter("fields", fields)
    }
}