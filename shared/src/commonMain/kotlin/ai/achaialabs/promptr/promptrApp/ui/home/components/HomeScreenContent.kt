package ai.achaialabs.promptr.promptrApp.ui.home.components

import ai.achaialabs.promptr.promptrApp.ui.CosmicLottieLoader
import ai.achaialabs.promptr.promptrApp.ui.home.HomeUiState
import ai.achaialabs.promptr.promptrApp.ui.model.FeedMediaUi
import ai.achaialabs.promptr.promptrApp.ui.model.PromptUi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    onPromptClick: (String) -> Unit = {}
) {

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {

        item {

            if (uiState.heroes.isNotEmpty()) {

                Spacer(Modifier.height(12.dp))
                HomeHeroSection(
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

        items(
            uiState.prompts.chunked(2)
        ) { rowItems ->

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

                if(rowItems.size == 1) {

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
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

    var currentTextIndex by remember {
        mutableStateOf(0)
    }

    LaunchedEffect(Unit) {

        while (true) {

            delay(1800)

            currentTextIndex =
                (currentTextIndex + 1) % cosmicTexts.size
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        // AMBIENT SPACE GLOW
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xF0D55900).copy(alpha = 0.10f),
                            Color.Transparent
                        ),
                        radius = 1200f
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CosmicLottieLoader()

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedContent(
                targetState = cosmicTexts[currentTextIndex],
                label = "cosmicText"
            ) { text ->

                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.3.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.92f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Entering the cosmic archive",
                style = MaterialTheme.typography.bodyMedium.copy(
                    letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.42f)
            )
        }
    }
}