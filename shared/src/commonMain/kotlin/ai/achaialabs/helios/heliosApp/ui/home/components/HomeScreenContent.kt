package ai.achaialabs.helios.heliosApp.ui.home.components

import ai.achaialabs.helios.heliosApp.ad.NativeAdCard
import ai.achaialabs.helios.heliosApp.ad.NativeAdState
import ai.achaialabs.helios.heliosApp.ui.CosmicLottieLoader
import ai.achaialabs.helios.heliosApp.ui.home.HomeUiState
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    onPlayClick: (String) -> Unit = {},
    onLikeClick: (String) -> Unit = {},
    onShareClick: (String) -> Unit = {},
    onPromptClick: (String) -> Unit = {},
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
    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {

        item {

            if (uiState.heroes.isNotEmpty()) {

                Spacer(Modifier.height(12.dp))
                HomeHero(
                    heroes = uiState.heroes,
                    onHeroClick = { hero ->

                    }
                )
            }

            Spacer(Modifier.height(20.dp))

            SectionTitle(
                title = "Discover Prompts"
            )

            Spacer(Modifier.height(20.dp))
        }

        val chunkedPrompts = uiState.prompts.chunked(2)

        itemsIndexed(chunkedPrompts) { index, rowItems ->

            Column {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),

                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    rowItems.forEach { prompt ->

                        PromptCard(

                            prompt = prompt,

                            modifier =
                                Modifier.weight(1f),

                            isPlaying =
                                uiState.activeVideoId ==
                                        prompt.id,

                            onPlayClick = {
                                onPlayClick(prompt.id)
                            },

                            onCardClick = {
                                onPromptClick(prompt.id)
                            },

                            onLikeClick = {
                                onLikeClick(prompt.id)
                            },

                            onShareClick = {
                                onShareClick(prompt.id)
                            }
                        )
                    }

                    if (rowItems.size == 1) {

                        Spacer(
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // SHOW AD AFTER EVERY 4 PROMPTS
                // each row = 2 prompts
                // every 2 rows = 4 prompts

                val shouldShowAd =
                    index != 0 &&
                            index % 2 == 1

                if (
                    shouldShowAd &&
                    uiState.nativeAdState is NativeAdState.Loaded
                ) {

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    NativeAdCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )
                }


                Spacer(Modifier.height(12.dp))
            }
        }
    }
}



@Composable
fun HomeScreenLoader(
    modifier: Modifier = Modifier
) {
    val cosmicTexts = remember {
        listOf(
            "Scanning galaxies...",
            "Exploring prompt universe...",
            "Decoding constellations...",
            "Traversing neural cosmos...",
            "Orbiting visual worlds...",
            "Discovering cinematic prompts...",
            "Synchronizing cosmic archives..."
        )
    }

    var currentTextIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1800)
            currentTextIndex = (currentTextIndex + 1) % cosmicTexts.size
        }
    }

    // Using Surface provides a clean, theme-aware background
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CosmicLottieLoader()

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedContent(
                targetState = cosmicTexts[currentTextIndex],
                label = "cosmicText"
            ) { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Entering the cosmic archive",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}