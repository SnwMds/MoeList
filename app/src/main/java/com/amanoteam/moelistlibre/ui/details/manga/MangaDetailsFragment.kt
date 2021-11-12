package com.amanoteam.moelistlibre.ui.details.manga

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
import com.amanoteam.moelistlibre.data.model.isManga
import com.amanoteam.moelistlibre.data.model.manga.MangaDetails
import com.amanoteam.moelistlibre.databinding.FragmentDetailsBinding
import com.amanoteam.moelistlibre.ui.base.BaseFragment
import com.amanoteam.moelistlibre.ui.main.MainViewModel
import com.amanoteam.moelistlibre.utils.Constants.RESPONSE_ERROR
import com.amanoteam.moelistlibre.utils.Extensions.openLink
import com.amanoteam.moelistlibre.utils.Extensions.setDrawables
import com.amanoteam.moelistlibre.utils.StringExtensions.formatGenre
import com.amanoteam.moelistlibre.utils.StringExtensions.formatMediaType
import com.amanoteam.moelistlibre.utils.StringExtensions.formatStatus
import com.amanoteam.moelistlibre.utils.UseCases.copyToClipBoard
import com.google.android.material.chip.Chip
import kotlinx.coroutines.flow.collectLatest
import java.text.NumberFormat
import java.util.*

class MangaDetailsFragment : BaseFragment<FragmentDetailsBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDetailsBinding
        get() = FragmentDetailsBinding::inflate
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: MangaDetailsViewModel by viewModels()
    private lateinit var adapterRelateds: RelatedsAdapter
    private var bottomSheetDialog: EditMangaFragment? = null

    override fun setup() {

        launchLifecycleStarted {
            mainViewModel.selectedId.collectLatest {
                it?.let {
                    binding.loading.show()
                    viewModel.getMangaDetails(it)
                }
            }
        }

        launchLifecycleStarted {
            viewModel.mangaDetails.collectLatest {
                it?.let { setMangaData(it) }
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

        hideAnimeViews()

        //Relateds
        adapterRelateds = RelatedsAdapter(
            safeContext,
            onClick = { _, item ->
                mainViewModel.selectId(item.node.id)
                if (!item.isManga()) mainActivity?.navigate(
                    idAction = R.id.action_animeDetailsFragment_self
                )
            }
        )
        binding.listRelateds.adapter = adapterRelateds

        binding.editFab.setOnClickListener {
            bottomSheetDialog?.show(parentFragmentManager, "Edit")
        }
    }

    private fun hideAnimeViews() {
        binding.apply {
            mediaType.setDrawables(start = R.drawable.ic_round_menu_book_24)
            seasonTitle.visibility = View.GONE
            season.visibility = View.GONE
            broadcastTitle.visibility = View.GONE
            broadcast.visibility = View.GONE
            durationTitle.visibility = View.GONE
            duration.visibility = View.GONE
            studiosTitle.text = getString(R.string.serialization)
            opening.visibility = View.GONE
            listOpening.visibility = View.GONE
            ending.visibility = View.GONE
            listEnding.visibility = View.GONE
        }
    }

    private fun setMangaData(mangaDetails: MangaDetails) {
        binding.loading.hide()

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.open_in_browser -> {
                    safeContext.openLink("https://myanimelist.net/manga/${mangaDetails.id}")
                    true
                }
                else -> false
            }
        }

        bottomSheetDialog = EditMangaFragment(
            mangaDetails.myListStatus,
            mangaDetails.id,
            mangaDetails.numChapters ?: 0,
            mangaDetails.numVolumes ?: 0
        )

        // Change FAB if entry not added
        if (mangaDetails.myListStatus == null) {
            binding.editFab.text = getString(R.string.add)
            binding.editFab.setIconResource(R.drawable.ic_round_add_24)
        } else {
            binding.editFab.text = getString(R.string.edit)
            binding.editFab.setIconResource(R.drawable.ic_round_edit_24)
        }

        binding.poster.load(mangaDetails.mainPicture?.medium)
        binding.poster.setOnClickListener {
            mainActivity?.navigate(
                idAction = R.id.action_global_fullPosterFragment,
                bundle = Bundle().apply { putString("poster_url", mangaDetails.mainPicture?.large) }
            )
        }
        binding.mainTitle.text = mangaDetails.title

        binding.mediaType.text = mangaDetails.mediaType?.formatMediaType(safeContext)

        binding.episodesChapters.text = if (mangaDetails.numChapters == 0) "?? ${getString(R.string.chapters)}"
        else "${mangaDetails.numChapters} ${getString(R.string.chapters)}"

        binding.volumes.text = if (mangaDetails.numVolumes == 0) "?? ${getString(R.string.volumes)}"
        else "${mangaDetails.numVolumes} ${getString(R.string.volumes)}"

        binding.status.text = mangaDetails.status?.formatStatus(safeContext)

        binding.score.text = mangaDetails.mean.toString()
        binding.synopsis.text = mangaDetails.synopsis

        // Genres chips
        val genres = mangaDetails.genres
        if (genres != null && binding.chipGroupGenres.childCount == 0) {
            for (genre in genres) {
                Chip(binding.chipGroupGenres.context).apply {
                    text = genre.name.formatGenre(safeContext)
                    binding.chipGroupGenres.addView(this)
                }
            }
        }

        // Stats
        binding.rankText.text = if (mangaDetails.rank == null) "N/A" else "#${mangaDetails.rank}"

        binding.numScoresText.text = NumberFormat.getInstance().format(mangaDetails.numScoringUsers)

        binding.membersText.text = NumberFormat.getInstance().format(mangaDetails.numListUsers)

        binding.popularityText.text = "#${mangaDetails.popularity}"

        // More info
        val synonymsText = mangaDetails.alternativeTitles?.synonyms?.joinToString(separator = ",\n")
        binding.synonyms.text = if (!synonymsText.isNullOrEmpty()) synonymsText else "─"

        val jpTitle = mangaDetails.alternativeTitles?.ja
        binding.jpTitle.text = if (!jpTitle.isNullOrEmpty()) jpTitle else "─"

        val unknown = getString(R.string.unknown)
        binding.startDate.text = if (!mangaDetails.startDate.isNullOrEmpty()) mangaDetails.startDate
        else unknown
        binding.endDate.text = if (!mangaDetails.endDate.isNullOrEmpty()) mangaDetails.endDate
        else unknown

        // Authors
        val authorsRoles = mutableListOf<String>()
        val authorsNames = mutableListOf<String>()
        val authorsSurnames = mutableListOf<String>()
        val authorsText = mutableListOf<String>()
        mangaDetails.authors?.forEach {
            authorsRoles.add(it.role)
            authorsNames.add(it.node.firstName)
            authorsSurnames.add(it.node.lastName)
        }
        mangaDetails.authors?.indices?.forEach {
            authorsText.add("${authorsNames[it]} ${authorsSurnames[it]} (${authorsRoles[it]})")
        }

        val authorsTextJoin = authorsText.joinToString(separator = ",\n")
        binding.authors.text = if (authorsTextJoin.isNotEmpty()) authorsTextJoin else unknown

        // Serialization
        val serialNames = mutableListOf<String>()
        mangaDetails.serialization?.forEach {
            serialNames.add(it.node.name)
        }
        val serialText = serialNames.joinToString(separator = ",\n")
        binding.studios.text = if (serialText.isNotEmpty()) serialText else unknown
    }

}