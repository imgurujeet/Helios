package ai.achaialabs.helios.heliosApp.ui.home.components

import ai.achaialabs.helios.heliosApp.ad.NativeAdCard
import ai.achaialabs.helios.heliosApp.ad.NativeAdState
import ai.achaialabs.helios.heliosApp.domain.filter.PromptFilter
import ai.achaialabs.helios.heliosApp.domain.model.HeroAction
import ai.achaialabs.helios.heliosApp.domain.model.HomeFeedType
import ai.achaialabs.helios.heliosApp.domain.model.Prompt
import ai.achaialabs.helios.heliosApp.ui.CosmicLottieLoader
import ai.achaialabs.helios.heliosApp.ui.home.HomeUiState
import ai.achaialabs.helios.heliosApp.ui.model.PromptUi
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.WavyProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay



sealed interface HomeFeedItem {

    data class PromptItem(
        val prompt: PromptUi
    ) : HomeFeedItem

    data class AdItem(
        val afterPrompt: Int
    ) : HomeFeedItem
}
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
    selectedFilter: HomeTab,
    onFilterSelected: (HomeTab) -> Unit,
    listState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    onPlayClick: (String) -> Unit = {},
    onLikeClick: (String) -> Unit = {},
    onShareClick: (PromptUi) -> Unit = {},
    onPromptClick: (String) -> Unit = {},
    onHeroClick: (HeroAction) -> Unit = {},
    onLoadMore: () -> Unit,
) {


    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            // If the last visible item is within 2 items of the end, trigger load
            lastVisibleItem != null && lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 2
        }
    }

    // Trigger the ViewModel action when the state condition is met
    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && !uiState.isPaginating) {
            onLoadMore()
        }
    }

    val feedItems = remember(
        uiState.prompts,
        uiState.adPositions,
        uiState.showAds,
    ) {

        buildList<HomeFeedItem> {

            uiState.prompts.forEachIndexed { index, prompt ->

                add(HomeFeedItem.PromptItem(prompt))

                if (
                    uiState.showAds &&
                    uiState.nativeAdState is NativeAdState.Loaded &&
                    (index + 1) in uiState.adPositions
                ) {
                    add(
                        HomeFeedItem.AdItem(
                            afterPrompt = index + 1
                        )
                    )
                }
            }
        }
    }


    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 12.dp,
            end = 12.dp,
            bottom = 100.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp
    ) {


        item(span = StaggeredGridItemSpan.FullLine) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                Spacer(Modifier.height(12.dp))

                if (uiState.heroes.isNotEmpty()) {
                    HomeHero(
                        heroes = uiState.heroes,
                        onHeroClick = { hero ->
                            onHeroClick(hero.action)
                        }
                    )
                }

                Spacer(Modifier.height(10.dp))
            }
        }

        item(span = StaggeredGridItemSpan.FullLine) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                PromptButtonGroup(
                    selected = selectedFilter,
                    onSelected = onFilterSelected
                )

                Spacer(Modifier.height(10.dp))
            }
        }



        if(uiState.isPromptRefreshing){
            item(span = StaggeredGridItemSpan.FullLine) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    LinearWavyProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xF0D55900),
                        amplitude =1f,

                        // Controls the wavelength frequency multiplier
                        wavelength = 20.dp,
                        stroke = Stroke(
                            width = with(LocalDensity.current) { 5.dp.toPx() },
                            cap = StrokeCap.Round
                        ),
                        waveSpeed = 5.dp
                    )
                }
            }
        }else{

            items(
                items = feedItems,
                key = {
                    when (it) {
                        is HomeFeedItem.PromptItem ->
                            it.prompt.id

                        is HomeFeedItem.AdItem ->
                            "ad_after_${it.afterPrompt}"
                    }
                },
                span = {
                    when (it) {
                        is HomeFeedItem.PromptItem ->
                            StaggeredGridItemSpan.SingleLane

                        is HomeFeedItem.AdItem ->
                            StaggeredGridItemSpan.FullLine
                    }
                }
            ) { item ->

                when (item) {

                    is HomeFeedItem.PromptItem -> {

                        val prompt = item.prompt

                        PromptCard(
                            prompt = prompt,
                            modifier = Modifier.fillMaxWidth(),
                            isPlaying = uiState.activeVideoId == prompt.id,
                            onPlayClick = { onPlayClick(prompt.id) },
                            onCardClick = { onPromptClick(prompt.id) },
                            onLikeClick = { onLikeClick(prompt.id) },
                            onShareClick = { onShareClick(prompt) }
                        )
                    }

                    is HomeFeedItem.AdItem -> {

                        NativeAdCard(
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

        }


            if (uiState.isPaginating) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally){
                            LoadingIndicator(modifier = Modifier.size(32.dp), color = Color(0xF0D55900))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Loading...", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                    }

                }
            }
        }
    }
}

