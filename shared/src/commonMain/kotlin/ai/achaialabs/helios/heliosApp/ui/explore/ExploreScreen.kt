package ai.achaialabs.helios.heliosApp.ui.explore

import ai.achaialabs.helios.heliosApp.ui.components.AdvancedResponsiveCarousel
import ai.achaialabs.helios.heliosApp.ui.components.CarouselLayoutMode
import ai.achaialabs.helios.heliosApp.ui.components.CustomCarouselConfig
import ai.achaialabs.helios.heliosApp.ui.explore.components.CategorySectionHeader
import ai.achaialabs.helios.heliosApp.ui.explore.components.ExploreHeader
import ai.achaialabs.helios.heliosApp.ui.model.CategoryUi
import ai.achaialabs.helios.heliosApp.ui.model.FeedMediaUi
import ai.achaialabs.helios.heliosApp.ui.model.PromptUi
import ai.achaialabs.helios.heliosApp.ui.navigation.ChromeState
import ai.achaialabs.helios.heliosApp.utils.ObserveScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.compose.viewmodel.koinViewModel

data class CategoryRowUi(
    val category: CategoryUi, // Using your exact model here
    val prompts: List<PromptUi>
)


val ThemeAmber = Color(0xFFF59E0B)
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExploreScreen(
    chromeState: ChromeState,
    viewModel: ExploreViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
    onViewAllClick: (categoryId: String, categoryName: String) -> Unit = { _, _ -> },
    onPromptClick: (
        promptId: String,
        categoryId: String
    ) -> Unit = { _, _ -> },
    onUnlockPremiumClick: () -> Unit = {}
) {

    val uiState by viewModel.uiState.collectAsState()
    val isPremium by viewModel.isPremium.collectAsStateWithLifecycle()
    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior()
    val listState = rememberLazyListState()


    ObserveScroll(
        listState = listState,
        chromeState = chromeState
    )

    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo

            val lastVisibleItem =
                layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            lastVisibleItem to layoutInfo.totalItemsCount
        }
            .distinctUntilChanged()
            .collect { (lastVisible, totalItems) ->

                // Trigger when user is close to the end
                if (lastVisible >= totalItems - 3) {
                    viewModel.loadMore()
                }
            }
    }
    LaunchedEffect(Unit){
        viewModel.viewAllOpened()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize().nestedScroll(
                scrollBehavior.nestedScrollConnection
                ),
        topBar = {
            ExploreHeader(
                isPremium= isPremium,
                onUnlockPremiumClick = onUnlockPremiumClick,
                scrollBehavior = scrollBehavior
            )


        }
    ) { paddingValues ->


        if (uiState.isInitialLoading) {
            // Full screen loading on very first launch
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularWavyProgressIndicator(
                    color = Color(0xF0D55900)
                )
            }
            return@Scaffold
        }



            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp) // Bottom padding for navbar
            ) {

                items(
                    items = uiState.categories,
                    key = { it.category.id }
                ) { categoryRow ->

                    CategorySectionHeader(
                        title = categoryRow.category.name,
                        onViewAllClick = {
                            viewModel.onCategoryOpened(
                                categoryId = categoryRow.category.id,
                                categoryName = categoryRow.category.name
                            )
                            onViewAllClick(categoryRow.category.id, categoryRow.category.name) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Using the Advanced UNIFIED carousel
                    AdvancedResponsiveCarousel(
                        items = categoryRow.prompts,
                        config = CustomCarouselConfig(
                            layoutMode = CarouselLayoutMode.ASYMMETRIC_START,
                            height = 200.dp,
                            mainWidthRatio = 0.40f,
                            minorWidthRatio = 0.20f,
                            pageSpacing = 16.dp,
                            outerPadding = 16.dp,
                            shapeProvider = { _, sizeFraction ->
                                val animatedCorner = androidx.compose.ui.unit.lerp(
                                    start = 100.dp, stop = 24.dp, fraction = sizeFraction
                                )
                                RoundedCornerShape(animatedCorner)
                            }
                        ),
                        onItemClick = { prompt ->
                            viewModel.onPromptOpened(
                                promptId = prompt.id,
                                categoryId = categoryRow.category.id,
                                categoryName = categoryRow.category.name
                            )
                            onPromptClick(
                                prompt.id,
                                categoryRow.category.id
                            )
                        }
                    ) { prompt, isFocused ->

                        ExploreCard(prompt = prompt)

                    }

                    Spacer(modifier = Modifier.height(25.dp))

                }
                if (uiState.isPaginating) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingIndicator(modifier = Modifier.size(32.dp), color = Color(0xF0D55900))
                        }

                    }
                }

            }

        }

}




@Composable
private fun ExploreCard(
    prompt: PromptUi,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.DarkGray)
    ) {
        //  Determine the correct URL directly inside the Composable
        val mediaUrl = when (val media = prompt.media) {
            is FeedMediaUi.Image -> media.imageUrl
            is FeedMediaUi.Video -> media.thumbnailUrl ?: media.videoUrl // Fallback if thumbnail is missing
        }

        AsyncImage(
            model = mediaUrl,
            contentDescription = prompt.title,
            contentScale = ContentScale.Crop, // Ensures the media fills the bounds correctly
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}
