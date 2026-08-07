package ai.achaialabs.helios.heliosApp.ui.favourite

import ai.achaialabs.helios.heliosApp.ui.home.components.PromptCard
import ai.achaialabs.helios.heliosApp.ui.mapper.toUi
import ai.achaialabs.helios.heliosApp.ui.navigation.ChromeState
import ai.achaialabs.helios.heliosApp.ui.viewall.ViewAllViewModel
import ai.achaialabs.helios.heliosApp.utils.ObserveScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemKey
import helios.shared.generated.resources.Res
import helios.shared.generated.resources.ic_back_spaceship
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteScreen(
    chromeState: ChromeState,
    onBackClick: () -> Unit,
    onPromptClick: (String) -> Unit,
    viewModel: FavouriteViewModel = koinViewModel() // Inject your ViewModel
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val gridState = rememberLazyStaggeredGridState()
    val lazyPagingItems = viewModel.favoritePrompts.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsState()

    // Keep your scroll observation
    ObserveScroll(staggeredGridState = gridState, chromeState = chromeState)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(uiState.title, fontWeight = FontWeight.Bold) },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        // Handle Empty State cleanly
        if (lazyPagingItems.itemCount == 0 && lazyPagingItems.loadState.refresh is LoadState.NotLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No liked prompts in the registry.", color = MaterialTheme.colorScheme.outline)
            }
        } else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                state = gridState,
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(10.dp),
                verticalItemSpacing = 4.dp,
            ) {
                items(
                    count = lazyPagingItems.itemCount,
                    key = lazyPagingItems.itemKey { it.id }
                ) { index ->
                    val prompt = lazyPagingItems[index]
                    if (prompt != null) {
                        PromptCard(
                            prompt = prompt.toUi(),
                            modifier = Modifier.padding(4.dp),
                            isPlaying = uiState.activeVideoId == prompt.id,
                            onPlayClick = { viewModel.onPlayClick(prompt.id) },
                            onLikeClick = { viewModel.onLikeClick(prompt.id) },
                            onCardClick = { onPromptClick(prompt.id) }
                        )
                    }
                }

                // Automatic loading indicator at the bottom
                if (lazyPagingItems.loadState.append is LoadState.Loading) {
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
}