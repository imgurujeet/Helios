package ai.achaialabs.promptr.promptrApp.ui.home

import ai.achaialabs.promptr.promptrApp.domain.model.FeedMedia
import ai.achaialabs.promptr.promptrApp.ui.home.components.CategorySection
import ai.achaialabs.promptr.promptrApp.ui.home.components.HomeScreenContent
import ai.achaialabs.promptr.promptrApp.ui.home.components.HomeScreenLoader
import ai.achaialabs.promptr.promptrApp.ui.home.components.HomeTopBar
import ai.achaialabs.promptr.promptrApp.ui.home.components.LoadingPromptGrid
import ai.achaialabs.promptr.promptrApp.ui.home.components.PromptCard
import ai.achaialabs.promptr.promptrApp.ui.home.components.SectionTitle
import ai.achaialabs.promptr.promptrApp.ui.navigation.ChromeState
import ai.achaialabs.promptr.promptrApp.utils.ObserveScroll
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun HomeScreen(
    chromeState: ChromeState,
    viewModel: HomeViewModel = koinViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior()
    val listState = rememberLazyListState()
    ObserveScroll(
        listState = listState,
        chromeState = chromeState
    )

    if (
        uiState.isLoading
    ) {
        HomeScreenLoader()
        return
    }


    Scaffold(
        modifier = Modifier.nestedScroll(
            scrollBehavior.nestedScrollConnection
        ),
        topBar = {
            HomeTopBar(
                userName = uiState.currentUser?.name,
                profileImageUrl = uiState.currentUser?.avatarUrl,
//                onProfileClick = chromeState::onProfileClick,
//                onSearchClick = chromeState::onSearchClick,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        HomeScreenContent(
            modifier = Modifier.padding(innerPadding),
            uiState = uiState,
            listState = listState,
            onPlayClick = viewModel::onPlayClick,
            onLikeClick = viewModel::onLikeClick
        )
    }
}




