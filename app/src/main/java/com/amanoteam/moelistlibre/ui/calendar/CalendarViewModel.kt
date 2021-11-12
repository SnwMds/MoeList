package com.amanoteam.moelistlibre.ui.calendar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amanoteam.moelistlibre.App
import com.amanoteam.moelistlibre.data.model.ApiParams
import com.amanoteam.moelistlibre.data.model.anime.AnimeSeasonal
import com.amanoteam.moelistlibre.utils.Constants
import com.amanoteam.moelistlibre.utils.Constants.ERROR_SERVER
import com.amanoteam.moelistlibre.utils.Constants.RESPONSE_ERROR
import com.amanoteam.moelistlibre.utils.Constants.RESPONSE_NONE
import com.amanoteam.moelistlibre.utils.Constants.RESPONSE_OK
import com.amanoteam.moelistlibre.utils.SeasonCalendar
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CalendarViewModel : ViewModel() {

    private val nsfw = MutableStateFlow(0)
    fun setNsfw(value: Int) {
        nsfw.value = value
        params.value.nsfw = nsfw.value
    }

    private val _mondayList = MutableStateFlow(mutableListOf<AnimeSeasonal>())
    val mondayList: StateFlow<List<AnimeSeasonal>> = _mondayList

    private val _tuesdayList = MutableStateFlow(mutableListOf<AnimeSeasonal>())
    val tuesdayList: StateFlow<List<AnimeSeasonal>> = _tuesdayList

    private val _wednesdayList = MutableStateFlow(mutableListOf<AnimeSeasonal>())
    val wednesdayList: StateFlow<List<AnimeSeasonal>> = _wednesdayList

    private val _thursdayList = MutableStateFlow(mutableListOf<AnimeSeasonal>())
    val thursdayList: StateFlow<List<AnimeSeasonal>> = _thursdayList

    private val _fridayList = MutableStateFlow(mutableListOf<AnimeSeasonal>())
    val fridayList: StateFlow<List<AnimeSeasonal>> = _fridayList

    private val _saturdayList = MutableStateFlow(mutableListOf<AnimeSeasonal>())
    val saturdayList: StateFlow<List<AnimeSeasonal>> = _saturdayList

    private val _sundayList = MutableStateFlow(mutableListOf<AnimeSeasonal>())
    val sundayList: StateFlow<List<AnimeSeasonal>> = _sundayList


    private val _response = MutableStateFlow(RESPONSE_NONE to "")
    val response: StateFlow<Pair<String, String>> = _response
    private val params = MutableStateFlow(
        ApiParams(
            sort = Constants.SORT_ANIME_NUM_USERS,
            nsfw = nsfw.value,
            fields = FIELDS,
            limit = 500
        )
    )

    fun getSeasonAnimes(page: String? = null) {
        viewModelScope.launch {
            val call = if (page == null) async { App.api.getSeasonalAnime(
                params = params.value,
                year = SeasonCalendar.currentYear,
                season = SeasonCalendar.currentSeasonStr
            ) } else {
                async {
                    App.api.getSeasonalAnime(page)
                }
            }

            val result = try {
                call.await()
            } catch (e: Exception) {
                Log.d("moelog", e.toString())
                null
            }

            when {
                result == null -> _response.value = RESPONSE_ERROR to ERROR_SERVER
                !result.error.isNullOrEmpty() -> _response.value = RESPONSE_ERROR to "${result.error}: ${result.message}"
                !result.message.isNullOrEmpty() -> _response.value = RESPONSE_ERROR to "${result.error}: ${result.message}"
                else -> {
                    result.data?.forEach {
                        it.node.broadcast?.let { broadcast ->
                            when (broadcast.dayOfTheWeek) {
                                Constants.MONDAY -> _mondayList.value.add(it)
                                Constants.TUESDAY -> _tuesdayList.value.add(it)
                                Constants.WEDNESDAY -> _wednesdayList.value.add(it)
                                Constants.THURSDAY -> _thursdayList.value.add(it)
                                Constants.FRIDAY -> _fridayList.value.add(it)
                                Constants.SATURDAY -> _saturdayList.value.add(it)
                                Constants.SUNDAY -> _sundayList.value.add(it)
                            }
                        }
                    }
                    _response.value = RESPONSE_OK to ""
                }
            }
        }
    }

    init {
        getSeasonAnimes()
    }

    companion object {
        private const val FIELDS = "broadcast,mean,start_season,status,media_type,num_episodes"
    }
}