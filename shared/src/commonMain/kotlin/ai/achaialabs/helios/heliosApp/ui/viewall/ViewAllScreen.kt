package ai.achaialabs.helios.heliosApp.ui.viewall

import ai.achaialabs.helios.heliosApp.ad.NativeAdCard
import ai.achaialabs.helios.heliosApp.ad.NativeAdState
import ai.achaialabs.helios.heliosApp.ui.home.components.PromptCard
import ai.achaialabs.helios.heliosApp.ui.model.PromptUi
import ai.achaialabs.helios.heliosApp.ui.navigation.ChromeState
import ai.achaialabs.helios.heliosApp.utils.ObserveScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import helios.shared.generated.resources.Res
import helios.shared.generated.resources.ic_back_spaceship
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewAllScreen(
    chromeState: ChromeState,
    categoryId: String,
    categoryName: String,
    viewModel: ViewAllViewModel = koinViewModel(
        key = categoryId,
        parameters = { parametersOf(categoryId, categoryName) }
    ),
    onBackClick: () -> Unit,
    onPromptClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    //val gridState = rememberLazyGridState()
    val gridState = rememberLazyStaggeredGridState()

    // FIXED: Maps to the new Grid version of ObserveScroll
    ObserveScroll(
        staggeredGridState = gridState,
        chromeState = chromeState
    )

    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index >= gridState.layoutInfo.totalItemsCount - 5
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { shouldLoadMore.value }
            .distinctUntilChanged()
            .collect { shouldLoad ->
                if (shouldLoad && !uiState.isLoading) {
                    viewModel.loadMore()
                }
            }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(uiState.categoryName, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_back_spaceship),
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(20.dp).rotate(
                                -90f
                            ),

                            )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->

        val gridItems = remember(
            uiState.prompts,
            uiState.nativeAdState,
            uiState.showAds
        ) {
            buildGridItems(
                prompts = uiState.prompts,
                showAds = uiState.showAds &&
                        uiState.nativeAdState is NativeAdState.Loaded
            )
        }
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            state = gridState,
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(10.dp),
            verticalItemSpacing = 10.dp,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                items = gridItems,
                key = {
                    when (it) {
                        is ViewAllGridItem.PromptItem -> it.prompt.id
                        ViewAllGridItem.AdItem -> "ad_${gridItems.indexOf(it)}"
                    }
                },
                span = { item ->

                    when (item) {
                        ViewAllGridItem.AdItem ->
                            StaggeredGridItemSpan.FullLine

                        else ->
                            StaggeredGridItemSpan.SingleLane
                    }
                }
            ){ item ->

                when (item) {

                    is ViewAllGridItem.PromptItem -> {

                        val prompt = item.prompt

                        PromptCard(
                            prompt = prompt,
                            modifier = Modifier.fillMaxWidth(),
                            isPlaying = uiState.activeVideoId == prompt.id,
                            onLikeClick = {
                                viewModel.onLikeClick(prompt.id)
                            },
                            onPlayClick = {
                                viewModel.onPlayClick(prompt.id)
                            },
                            onCardClick = {
                                onPromptClick(prompt.id)
                            }
                        )
                    }

                    ViewAllGridItem.AdItem -> {

                        NativeAdCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }



            if (uiState.isLoading) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        LoadingIndicator(
                            modifier = Modifier.size(56.dp),
                            color = Color(0xF0D55900),
                        )
                    }
                }
            }
        }
    }
}


private fun buildGridAdPositions(
    totalItems: Int,
    firstAdAfter: Int = 8,
    minGap: Int = 8,
    maxGap: Int = 14
): Set<Int> {

    val random = kotlin.random.Random(totalItems)

    val positions = mutableSetOf<Int>()

    var current = firstAdAfter

    while (current < totalItems - 2) {
        positions += current
        current += random.nextInt(minGap, maxGap + 1)
    }

    return positions
}


private fun buildGridItems(
    prompts: List<PromptUi>,
    showAds: Boolean
): List<ViewAllGridItem> {

    if (!showAds) {
        return prompts.map { ViewAllGridItem.PromptItem(it) }
    }

    val adPositions = buildGridAdPositions(
        totalItems = prompts.size
    )

    val result = mutableListOf<ViewAllGridItem>()

    prompts.forEachIndexed { index, prompt ->

        result += ViewAllGridItem.PromptItem(prompt)

        if (index in adPositions) {
            result += ViewAllGridItem.AdItem
        }
    }

    return result
}

sealed interface ViewAllGridItem {

    data class PromptItem(
        val prompt: PromptUi
    ) : ViewAllGridItem

    data object AdItem : ViewAllGridItem
}