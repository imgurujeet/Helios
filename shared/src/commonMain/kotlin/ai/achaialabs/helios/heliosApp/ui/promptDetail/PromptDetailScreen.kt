package ai.achaialabs.helios.heliosApp.ui.promptDetail

import ai.achaialabs.helios.heliosApp.ad.AdManager
import ai.achaialabs.helios.heliosApp.ad.RewardedAdState
import ai.achaialabs.helios.heliosApp.domain.model.FeedMedia
import ai.achaialabs.helios.heliosApp.domain.model.Prompt
import ai.achaialabs.helios.heliosApp.ui.CosmicLottieLoader
import ai.achaialabs.helios.heliosApp.ui.model.FeedMediaUi
import ai.achaialabs.helios.heliosApp.ui.promptDetail.components.CosmicGate
import ai.achaialabs.helios.heliosApp.ui.promptDetail.components.HeroImageSection
import ai.achaialabs.helios.heliosApp.ui.promptDetail.components.LaunchControlCard
import ai.achaialabs.helios.heliosApp.ui.promptDetail.components.PromptContentCard
import ai.achaialabs.helios.heliosApp.ui.promptDetail.components.PromptDetailTopBar
import ai.achaialabs.helios.heliosApp.ui.promptDetail.components.PromptInstructionCard
import ai.achaialabs.helios.heliosApp.ui.share.components.PromptShareCard
import ai.achaialabs.helios.heliosApp.ui.share.manager.ShareManager
import ai.achaialabs.helios.heliosApp.ui.share.model.SharePromptUi
import ai.achaialabs.helios.heliosApp.utils.rememberExternalAppLauncher
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf



val CosmicDarkBg = Color(0xFF05070B)
val CosmicAccent = Color(0xFFF59E0B)
val GlassBorder = Color.White.copy(alpha = 0.12f)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PromptDetailScreen(
    categoryId: String?,
    promptId: String, // The ID passed from your navigation arguments
    viewModel: PromptDetailViewModel = koinViewModel(
        key = categoryId,
        parameters = { parametersOf(categoryId) }
    ),
    onBackClick: () -> Unit,
    onSubScribeClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val clipboardManager = LocalClipboardManager.current
    var sharePrompt by remember {
        mutableStateOf<Prompt?>(null)
    }
    var shareCardReady by remember {
        mutableStateOf(false)
    }
    val graphicsLayer = rememberGraphicsLayer()
    // 2. Get our custom KMP App Launcher
    val appLauncher = rememberExternalAppLauncher()
    val shareManager = remember {
        ShareManager()
    }
    LaunchedEffect(promptId) {
        viewModel.initializeFeed(clickedPromptId = promptId)
    }

    LaunchedEffect(promptId) {
        viewModel.onPromptDetailOpened()
    }

    LaunchedEffect(
        sharePrompt,
        shareCardReady
    ) {

        if (
            sharePrompt != null &&
            shareCardReady
        ) {

            delay(500)

            val imageBitmap =
                graphicsLayer.toImageBitmap()

            shareManager.sharePrompt(
                imageBitmap
            )

            sharePrompt = null

            shareCardReady = false
        }
    }


    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            PromptDetailTopBar(
                onBackClick = onBackClick,
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->

        Spacer(modifier = Modifier.padding(10.dp))
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // 2. Wait for Room to return the prompts before rendering the Pager
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularWavyProgressIndicator(color = CosmicAccent)
                }
            } else if (uiState.prompts.isNotEmpty()) {

                // 3. Initialize Pager with the correct starting page
                val pagerState = rememberPagerState(
                    initialPage = uiState.initialPageIndex,
                    pageCount = { uiState.prompts.size + 1 }
                )

                LaunchedEffect(uiState.initialPageIndex) {
                    if (pagerState.currentPage != uiState.initialPageIndex) {
                        pagerState.scrollToPage(uiState.initialPageIndex)
                    }
                }
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 0.dp),
                    pageSpacing = 16.dp
                ) { page ->

                    // Check if we are still looking at real prompts
                    if (page < uiState.prompts.size) {

                        val currentPrompt = uiState.prompts[page]
                        val listState = rememberLazyListState()
                        val isFocused = pagerState.currentPage == page

                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                top = paddingValues.calculateTopPadding(),
                                bottom = 120.dp,
                                start = 16.dp,
                                end = 16.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Column {
                                    HeroImageSection(
                                        currentPrompt = currentPrompt,
                                        modifier = Modifier
                                            .fillParentMaxHeight(0.8f)
                                            .fillMaxWidth(),
                                        onLikeClick = { viewModel.onLikeClick(currentPrompt.id) },
                                        onPlayClick = {
                                            viewModel.onPlayClick(currentPrompt.id)
                                        },
                                        isPlaying = isFocused,
                                        onShareClick = {
                                            sharePrompt = currentPrompt
                                        }
                                    )
//                                    Spacer(modifier = Modifier.height(16.dp))
//                                    Text(
//                                        text = currentPrompt.content.title.orEmpty(),
//                                        style = MaterialTheme.typography.titleMedium.copy(
//                                            fontWeight = FontWeight.Bold
//                                        ),
//                                        color = MaterialTheme.colorScheme.onBackground,
//                                        modifier = Modifier.padding(horizontal = 16.dp)
//                                    )
                                }

                            }



                            // 4. Tools pass in dynamically. Highlight updates automatically!
                            // --- ACCESS BLOCK ---
                            item {
                                CosmicGate(
                                    isPremium = currentPrompt.isPremium,
                                    isPro = uiState.isProUser,
                                    isAdLoading =
                                        uiState.rewardedAdState is RewardedAdState.Loading,
                                    isRevealed = uiState.revealedPrompts.contains(currentPrompt.id),
                                    onRevealClick = {
                                        viewModel.revealPrompt(
                                            currentPrompt.id
                                        )
                                    },
                                    onSubscribeClick = {
                                       onSubScribeClick()
                                    }
                                ) {

                                    Column {

                                        LaunchControlCard(
                                            recommendedToolId = currentPrompt.recommendedTools?.id.orEmpty(),
                                            tools = uiState.tools,
                                            isLoading = uiState.isToolsLoading,
                                            onToolClick = {
                                                clipboardManager.setText(
                                                    AnnotatedString(
                                                        currentPrompt.content.description.orEmpty()
                                                    )
                                                )

                                                appLauncher.launch(
                                                    packageId = it.id,
                                                    fallbackUrl = "https://google.com/search?q=${it.name}",
                                                    promptText = currentPrompt.content.promptText.orEmpty()
                                                )
                                            }
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        PromptContentCard(
                                            prompt = currentPrompt.content.promptText
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))
                                        PromptInstructionCard(
                                            text = currentPrompt.content.description.toString()
                                        )

                                    }
                                }
                            }

                            item { TagsSection(currentPrompt = currentPrompt) }
                           // item { HeliosRecommendationCard() }


                        }

                    } else {

                        EndOfUniverseCard()
                    }


                }
            }


        }

        sharePrompt?.let { prompt ->
            val mediaUi = remember(prompt.media) {
                when (val media = prompt.media) {
                    is FeedMedia.Image -> FeedMediaUi.Image(media.imageUrl, media.aspectRatio)
                    is FeedMedia.Video -> FeedMediaUi.Video(
                        videoUrl = media.videoUrl,
                        thumbnailUrl = media.thumbnailUrl,
                        durationText = media.durationMs.toString(),
                        aspectRatio = media.aspectRatio
                    )
                    else -> error("Unsupported media type")
                }
            }

            val imageUrl = when (mediaUi) {

                is FeedMediaUi.Image -> {
                    mediaUi.imageUrl
                }

                is FeedMediaUi.Video -> {
                    mediaUi.thumbnailUrl
                }
            }


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
                   
            ) {

                PromptShareCard(
                    data = SharePromptUi(
                        username = "Gurujeet",
                        promptTitle = prompt.content.title.orEmpty(),
                        imageUrl = imageUrl.orEmpty(),
                        appLink = "https://play.google.com/store/apps/details?id=ai.achaialabs.helios"
                    ),

                    onImageLoaded = {

                        shareCardReady = true
                    },
                    modifier = Modifier
                        // THIS is where the capture magic happens.
                        // Now it only captures the exact dimensions of the square card!
                        .drawWithContent {
                            graphicsLayer.record {
                                this@drawWithContent.drawContent()
                            }
                            drawLayer(graphicsLayer)
                        }
                )
            }
        }
    }
}


@Composable
fun EndOfUniverseCard() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // You can replace this emoji with an actual Image/Icon if you have a cool rocket SVG!
        CosmicLottieLoader(
            file = "files/astronaut_hello.lottie",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "You've reached the edge.",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "We are currently exploring the galaxy to find more prompts for you. Check back soon!",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}


@Composable
fun TagsSection(currentPrompt: Prompt) {
    Column(
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {
        Text(
            text = "Cosmic Tags",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(14.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            currentPrompt.metadata.tags.forEach { tag ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CosmicDarkBg.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, GlassBorder)
                ) {
                    Text(
                        text = "#$tag",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}


@Composable
fun HeliosRecommendationCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CosmicAccent.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = CosmicAccent.copy(alpha = 0.05f)
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "HELIOS AI",
                    style = MaterialTheme.typography.labelMedium.copy(
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = CosmicAccent
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Generate a Variant",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Let Helios adapt this prompt for your specific use case.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    lineHeight = 18.sp
                )
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(CosmicAccent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = CosmicDarkBg,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
