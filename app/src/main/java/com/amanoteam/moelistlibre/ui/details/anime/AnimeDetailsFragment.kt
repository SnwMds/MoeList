package com.amanoteam.moelistlibre.ui.details.anime

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.TooltipCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import coil.load
import com.amanoteam.moelistlibre.R
import com.amanoteam.moelistlibre.adapter.RelatedsAdapter
import com.amanoteam.moelistlibre.adapter.ThemesAdapter
import com.amanoteam.moelistlibre.data.model.anime.AnimeDetails
import com.amanoteam.moelistlibre.data.model.isManga
import com.amanoteam.moelistlibre.databinding.FragmentDetailsBinding
import com.amanoteam.moelistlibre.ui.base.BaseFragment
import com.amanoteam.moelistlibre.ui.main.MainViewModel
import com.amanoteam.moelistlibre.utils.Constants.RESPONSE_ERROR
import com.amanoteam.moelistlibre.utils.Extensions.openLink
import com.amanoteam.moelistlibre.utils.StringExtensions.formatGenre
import com.amanoteam.moelistlibre.utils.StringExtensions.formatMediaType
import com.amanoteam.moelistlibre.utils.StringExtensions.formatSeason
import com.amanoteam.moelistlibre.utils.StringExtensions.formatSource
import com.amanoteam.moelistlibre.utils.StringExtensions.formatStatus
import com.amanoteam.moelistlibre.utils.StringExtensions.formatWeekday
import com.amanoteam.moelistlibre.utils.UseCases.copyToClipBoard
import com.google.android.material.chip.Chip

import kotlinx.coroutines.flow.collectLatest
import java.text.NumberFormat
import java.util.*

class AnimeDetailsFragment : BaseFragment<FragmentDetailsBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDetailsBinding
        get() = FragmentDetailsBinding::inflate
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: AnimeDetailsViewModel by viewModels()
    private lateinit var adapterRelateds: RelatedsAdapter
    private var bottomSheetDialog: EditAnimeFragment? = null

    override fun setup() {

        launchLifecycleStarted {
            mainViewModel.selectedId.collectLatest {
                it?.let {
                    binding.loading.show()
                    viewModel.getAnimeDetails(it)
                }
            }
        }

        launchLifecycleStarted {
            viewModel.animeDetails.collectLatest {
                it?.let { setAnimeData(it) }
            }
        }

        launchLifecycleStarted {
            viewModel.relateds.collectLatest {
                if (it.isNotEmpty()) {
                    binding.relateds.visibility = View.VISIBLE
                    binding.listRelateds.visibility = View.VISIBLE
                    adapterRelateds.setData(it)
                } else {
                    binding.relateds.visibility = View.GONE
                    binding.listRelateds.visibility = View.GONE
                }
            }
        }

        launchLifecycleStarted {
            viewModel.response.collectLatest {
                if (it.first == RESPONSE_ERROR) {
                    showSnackbar(it.second)
                }
            }
        }

        initUI()
    }

    private fun initUI() {
        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }

        //Title
        binding.mainTitle.setOnLongClickListener {
            binding.mainTitle.text.toString().copyToClipBoard(safeContext)
            true
        }

        //Synopsis
        binding.synopsisIcon.setOnClickListener {
            if (binding.synopsis.maxLines == 5) {
                binding.synopsis.maxLines = Int.MAX_VALUE
                binding.synopsisIcon.setImageResource(R.drawable.ic_round_keyboard_arrow_up_24)
            }
            else {
                binding.synopsis.maxLines = 5
                binding.synopsisIcon.setImageResource(R.drawable.ic_round_keyboard_arrow_down_24)
            }
        }

        //Tooltips
        TooltipCompat.setTooltipText(binding.rankText, getString(R.string.top_ranked))
        binding.rankText.setOnClickListener { it.performLongClick() }
        TooltipCompat.setTooltipText(binding.membersText, getString(R.string.members))
        binding.membersText.setOnClickListener { it.performLongClick() }
        TooltipCompat.setTooltipText(binding.numScoresText, getString(R.string.users_scores))
        binding.numScoresText.setOnClickListener { it.performLongClick() }
        TooltipCompat.setTooltipText(binding.popularityText, getString(R.string.popularity))
        binding.popularityText.setOnClickListener { it.performLongClick() }

        hideMangaViews()

        //Relateds
        adapterRelateds = RelatedsAdapter(
            safeContext,
            onClick = { _, item ->
                mainViewModel.selectId(item.node.id)
                if (item.isManga()) mainActivity?.navigate(
                    idAction = R.id.action_mangaDetailsFragment_self
                )
            }
        )
        binding.listRelateds.adapter = adapterRelateds

        binding.editFab.setOnClickListener {
            bottomSheetDialog?.show(parentFragmentManager, "Edit")
        }
    }

    private fun hideMangaViews() {
        binding.apply {
            authorsTitle.visibility = View.GONE
            authors.visibility = View.GONE
            volumesTitle.visibility = View.GONE
            volumes.visibility = View.GONE
            dividerVolumes.visibility = View.GONE
        }
    }

    private fun setAnimeData(animeDetails: AnimeDetails) {
        binding.detailsScroll.smoothScrollTo(0, 0)
        binding.loading.hide()

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.open_in_browser -> {
                    safeContext.openLink("https://myanimelist.net/anime/${animeDetails.id}")
                    true
                }
                else -> false
            }
        }

        bottomSheetDialog = EditAnimeFragment(
            myListStatus = animeDetails.myListStatus,
            animeId = animeDetails.id,
            numEpisodes = animeDetails.numEpisodes ?: 0
        )

        // Change FAB if entry not added
        if (animeDetails.myListStatus == null) {
            binding.editFab.text = getString(R.string.add)
            binding.editFab.setIconResource(R.drawable.ic_round_add_24)
        } else {
            binding.editFab.text = getString(R.string.edit)
            binding.editFab.setIconResource(R.drawable.ic_round_edit_24)
        }

        binding.poster.load(animeDetails.mainPicture?.medium)
        binding.poster.setOnClickListener {
            mainActivity?.navigate(
                idAction = R.id.action_global_fullPosterFragment,
                bundle = Bundle().apply { putString("poster_url", animeDetails.mainPicture?.large) }
            )
        }
        binding.mainTitle.text = animeDetails.title

        binding.mediaType.text = animeDetails.mediaType?.formatMediaType(safeContext)

        binding.episodesChapters.text = if (animeDetails.numEpisodes == 0) "?? ${getString(R.string.episodes)}"
        else "${animeDetails.numEpisodes} ${getString(R.string.episodes)}"

        binding.status.text = animeDetails.status?.formatStatus(safeContext)

        binding.score.text = animeDetails.mean.toString()
        binding.synopsis.text = animeDetails.synopsis

        // Genres chips
        val genres = animeDetails.genres
        if (genres != null && binding.chipGroupGenres.childCount == 0) {
            for (genre in genres) {
                Chip(binding.chipGroupGenres.context).apply {
                    text = genre.name.formatGenre(safeContext)
                    binding.chipGroupGenres.addView(this)
                }
            }
        }

        // Stats
        binding.rankText.text = if (animeDetails.rank == null) "N/A" else "#${animeDetails.rank}"

        binding.numScoresText.text = NumberFormat.getInstance().format(animeDetails.numScoringUsers)

        binding.membersText.text = NumberFormat.getInstance().format(animeDetails.numListUsers)

        binding.popularityText.text = "#${animeDetails.popularity}"

        // More info
        val synonymsText = animeDetails.alternativeTitles?.synonyms?.joinToString(separator = ",\n")
        binding.synonyms.text = if (!synonymsText.isNullOrEmpty()) synonymsText else "─"

        val jpTitle = animeDetails.alternativeTitles?.ja
        binding.jpTitle.text = if (!jpTitle.isNullOrEmpty()) jpTitle else "─"

        val unknown = getString(R.string.unknown)
        binding.startDate.text = if (!animeDetails.startDate.isNullOrEmpty()) animeDetails.startDate
        else unknown
        binding.endDate.text = if (!animeDetails.endDate.isNullOrEmpty()) animeDetails.endDate
        else unknown

        val year = animeDetails.startSeason?.year
        val season = animeDetails.startSeason?.season?.formatSeason(safeContext)
        val seasonText = "$season $year"
        binding.season.text = if (animeDetails.startSeason != null) seasonText else unknown

        val weekDay = animeDetails.broadcast?.dayOfTheWeek?.formatWeekday(safeContext)
        val startTime = animeDetails.broadcast?.startTime
        binding.broadcast.text = if (animeDetails.broadcast!=null) "$weekDay $startTime (JST)"
        else unknown

        val duration = animeDetails.averageEpisodeDuration?.div(60)
        val durationText = "$duration ${getString(R.string.minutes_abbreviation)}."
        binding.duration.text = if (duration == 0) unknown else durationText

        binding.source.text = animeDetails.source?.formatSource(safeContext)

        val studiosNames = mutableListOf<String>()
        animeDetails.studios?.forEach {
            studiosNames.add(it.name)
        }
        val studiosText = studiosNames.joinToString(separator = ",\n")
        binding.studios.text = if (studiosText.isNotEmpty()) studiosText else unknown

        if (animeDetails.openingThemes.isNullOrEmpty()) {
            binding.opening.visibility = View.GONE
            binding.listOpening.visibility = View.GONE
        } else {
            binding.opening.visibility = View.VISIBLE
            binding.listOpening.visibility = View.VISIBLE
            binding.listOpening.adapter = ThemesAdapter(safeContext).apply {
                setData(animeDetails.openingThemes)
            }
        }

        if (animeDetails.endingThemes.isNullOrEmpty()) {
            binding.ending.visibility = View.GONE
            binding.listEnding.visibility = View.GONE
        } else {
            binding.ending.visibility = View.VISIBLE
            binding.listEnding.visibility = View.VISIBLE
            binding.listEnding.adapter = ThemesAdapter(safeContext).apply {
                setData(animeDetails.endingThemes)
            }
        }
    }

}