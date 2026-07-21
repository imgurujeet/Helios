package ai.achaialabs.helios.heliosApp.ui.home

import ai.achaialabs.helios.heliosApp.ui.home.components.HomeScreenContent
import ai.achaialabs.helios.heliosApp.ui.home.components.HomeScreenLoader
import ai.achaialabs.helios.heliosApp.ui.home.components.HomeTopBar
import ai.achaialabs.helios.heliosApp.ui.home.components.LoadingPromptCard
import ai.achaialabs.helios.heliosApp.ui.home.components.LoadingPromptGrid
import ai.achaialabs.helios.heliosApp.ui.navigation.ChromeState
import ai.achaialabs.helios.heliosApp.ui.navigation.PromptDetail
import ai.achaialabs.helios.heliosApp.ui.navigation.Search
import ai.achaialabs.helios.heliosApp.utils.ObserveScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun HomeScreen(
    chromeState: ChromeState,
   // onSearchClick : () -> Unit,
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
        LoadingPromptGrid()
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
                 onSearchClick = {
                     chromeState.navigateTo(Search)
                 },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        HomeScreenContent(
            modifier = Modifier.padding(innerPadding),
            uiState = uiState,
            listState = listState,
            onPlayClick = viewModel::onPlayClick,
            onLikeClick = viewModel::onLikeClick,
            onShareClick ={ },
            onPromptClick = { promptId ->
                chromeState.navigateTo(PromptDetail(promptId))
            },
            onLoadMore = viewModel::loadMore
        )
    }
}




