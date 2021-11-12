package com.amanoteam.moelistlibre.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.amanoteam.moelistlibre.R
import com.amanoteam.moelistlibre.adapter.CalendarAnimeAdapter
import com.amanoteam.moelistlibre.databinding.FragmentCalendarBinding
import com.amanoteam.moelistlibre.ui.base.BaseFragment
import com.amanoteam.moelistlibre.ui.main.MainViewModel
import com.amanoteam.moelistlibre.utils.Constants
import com.google.android.material.transition.MaterialFade
import kotlinx.coroutines.flow.collectLatest

class CalendarFragment : BaseFragment<FragmentCalendarBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCalendarBinding
        get() = FragmentCalendarBinding::inflate
    private val viewModel: CalendarViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: CalendarAnimeAdapter
    private var weekDay = Constants.MONDAY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString("week_day", Constants.MONDAY)?.let { weekDay = it }
        exitTransition = MaterialFade()
    }

    override fun setup() {
        adapter = CalendarAnimeAdapter(safeContext,
            onClick = { _, item ->
                mainViewModel.selectId(item.node.id)
                mainActivity?.navigate(
                    idAction = R.id.action_hostCalendarFragment_to_animeDetailsFragment
                )
            }
        )
        binding.list.adapter = adapter

        launchLifecycleStarted {
            viewModel.response.collectLatest {
                if (it.first == Constants.RESPONSE_OK) {
                    binding.loading.hide()
                    collectList()
                }
            }
        }

    }

    private fun collectList() {
        launchLifecycleStarted {
            when (weekDay) {
                Constants.MONDAY -> viewModel.mondayList.collectLatest { adapter.setData(it) }
                Constants.TUESDAY -> viewModel.tuesdayList.collectLatest { adapter.setData(it) }
                Constants.WEDNESDAY -> viewModel.wednesdayList.collectLatest { adapter.setData(it) }
                Constants.THURSDAY -> viewModel.thursdayList.collectLatest { adapter.setData(it) }
                Constants.FRIDAY -> viewModel.fridayList.collectLatest { adapter.setData(it) }
                Constants.SATURDAY -> viewModel.saturdayList.collectLatest { adapter.setData(it) }
                Constants.SUNDAY -> viewModel.sundayList.collectLatest { adapter.setData(it) }
            }
        }
    }
}