package ai.achaialabs.helios.heliosApp.ui.home

import ai.achaialabs.helios.heliosApp.domain.model.FeedMedia
import ai.achaialabs.helios.heliosApp.domain.model.HeroAction
import ai.achaialabs.helios.heliosApp.domain.model.Prompt
import ai.achaialabs.helios.heliosApp.firebase.analytics.AnalyticsService
import ai.achaialabs.helios.heliosApp.ui.home.components.HomeScreenContent
import ai.achaialabs.helios.heliosApp.ui.home.components.HomeTopBar
import ai.achaialabs.helios.heliosApp.ui.home.components.LoadingPromptCard
import ai.achaialabs.helios.heliosApp.ui.home.components.LoadingPromptGrid
import ai.achaialabs.helios.heliosApp.ui.model.FeedMediaUi
import ai.achaialabs.helios.heliosApp.ui.model.PromptUi
import ai.achaialabs.helios.heliosApp.ui.navigation.ChromeState
import ai.achaialabs.helios.heliosApp.ui.navigation.Profile
import ai.achaialabs.helios.heliosApp.ui.navigation.PromptDetail
import ai.achaialabs.helios.heliosApp.ui.navigation.Search
import ai.achaialabs.helios.heliosApp.ui.navigation.ViewAll
import ai.achaialabs.helios.heliosApp.ui.share.components.PromptShareCard
import ai.achaialabs.helios.heliosApp.ui.share.manager.ShareManager
import ai.achaialabs.helios.heliosApp.ui.share.model.SharePromptUi
import ai.achaialabs.helios.heliosApp.utils.ObserveScroll
import ai.achaialabs.helios.heliosApp.utils.rememberExternalAppLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    chromeState: ChromeState,
   // onSearchClick : () -> Unit,
    onProIconClick: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val isPro by viewModel.isPremium.collectAsStateWithLifecycle()
    val promptId = ""

    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior()
    val listState = rememberLazyStaggeredGridState()
    val uriHandler = LocalUriHandler.current
    ObserveScroll(
        staggeredGridState = listState,
        chromeState = chromeState
    )

    var showPreparingShare by remember {
        mutableStateOf(false)
    }
    var sharePrompt by remember {
        mutableStateOf<PromptUi?>(null)
    }

    var shareCardReady by remember {
        mutableStateOf(false)
    }

    val graphicsLayer = rememberGraphicsLayer()
    val shareManager = remember {
        ShareManager()
    }

    LaunchedEffect(
        sharePrompt,
        shareCardReady
    ) {

        if (
            sharePrompt != null &&
            shareCardReady
        ) {
            showPreparingShare = false
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

    val analytics: AnalyticsService = koinInject()

    LaunchedEffect(Unit) {
        analytics.logEvent(
            name = "screen_view",
            params = mapOf(
                "screen_name" to "Home"
            )
        )
    }

    LaunchedEffect(Unit) {
        viewModel.onHomeOpened()
    }



    Scaffold(
        modifier = Modifier.nestedScroll(
            scrollBehavior.nestedScrollConnection
        ),


        topBar = {
            if (!uiState.isLoading) {
                HomeTopBar(
                    userName = uiState.currentUser?.name,
                    profileImageUrl = uiState.currentUser?.avatarUrl,
                    onProfileClick = {
                        viewModel.onProfileOpened()
                        chromeState.navigateTo(Profile)
                    },
                    onSearchClick = {
                        viewModel.onSearchOpened()
                        chromeState.navigateTo(Search)
                    },
                    isPro = isPro,
                    onProIconClick = onProIconClick,
                    scrollBehavior = scrollBehavior

                )
            }
        }
    ) { innerPadding ->
        if (
            uiState.isLoading
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                LoadingIndicator(
                    modifier = Modifier.size(56.dp),
                    color = Color(0xF0D55900),
                )
            }

        }else{

            HomeScreenContent(
                modifier = Modifier.padding(innerPadding),
                uiState = uiState,
                listState = listState,
                selectedFilter = uiState.selectedTab,
                onFilterSelected = viewModel::onFeedSelected,
                onPlayClick = viewModel::onPlayClick,
                onLikeClick = viewModel::onLikeClick,
                onShareClick = { prompt ->
                    viewModel.onSharePrompt(prompt)
                    sharePrompt = prompt
                },
                onPromptClick = { promptId ->
                    viewModel.onPromptOpened(promptId)
                    chromeState.navigateTo(PromptDetail(promptId))
                },
                onHeroClick = { action ->


                    when (action) {

                        is HeroAction.OpenPrompt -> {
                            viewModel.onHeroClicked("prompt")
                            chromeState.navigateTo(
                                PromptDetail(promptId = action.promptId)
                            )
                        }

                        is HeroAction.OpenCategory -> {
                            viewModel.onHeroClicked(action.categoryName)
                            chromeState.navigateTo(
                                ViewAll(
                                    categoryId = action.categoryId,
                                    categoryName = action.categoryName
                                )
                            )
                        }

                        is HeroAction.OpenUrl -> {
                            viewModel.onHeroClicked("URL:${action.url}")
                            uriHandler.openUri(action.url)
                        }

                        is HeroAction.OpenSearch -> {
                            viewModel.onHeroClicked("search")
                            chromeState.navigateTo(
                                Search
                            )
                        }

                        is HeroAction.OpenScreen -> {
                            // Handle custom screens
                        }

                        HeroAction.None -> Unit
                    }
                },
                onLoadMore = viewModel::loadMore,
            )

            if (showPreparingShare) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            LoadingIndicator(
                                modifier = Modifier.size(56.dp),
                                color = Color(0xF0D55900),
                            )

                            Spacer(Modifier.height(16.dp))

                            Text("Preparing image...")
                        }
                    }
                }
            }
        }

    sharePrompt?.let { prompt ->


        val imageUrl = when (prompt.media) {

            is FeedMediaUi.Image -> {
                prompt.media.imageUrl
            }

            is FeedMediaUi.Video -> {
                prompt.media.thumbnailUrl
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent),
            contentAlignment = Alignment.Center

        ) {

            PromptShareCard(
                data = SharePromptUi(
                    username = uiState.currentUser?.name ?: "Helios User",
                    promptTitle = prompt.title.orEmpty(),
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




