package ai.achaialabs.helios.heliosApp.ui.search

import ai.achaialabs.helios.heliosApp.ui.CosmicLottieLoader
import ai.achaialabs.helios.heliosApp.ui.home.components.PromptCard
import ai.achaialabs.helios.heliosApp.ui.mapper.toUi
import ai.achaialabs.helios.heliosApp.ui.navigation.ChromeState
import ai.achaialabs.helios.heliosApp.ui.promptDetail.CosmicAccent
import ai.achaialabs.helios.heliosApp.ui.promptDetail.CosmicDarkBg
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemKey
import org.koin.compose.viewmodel.koinViewModel

// Replace the Android import with this for CMP


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    chromeState: ChromeState,
    viewModel: SearchViewModel = koinViewModel(),
    onPromptClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val lazyPagingItems = viewModel.searchResults.collectAsLazyPagingItems()
    val query by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            CosmicTopSearchBar(
                query = query,
                onQueryChange = viewModel::onQueryChange,
                onBackClick = {
                    onBackClick()
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (query.isBlank()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Hello buddy, what's on your mind?",
                        color = MaterialTheme.colorScheme.outline,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                // Handle different loading states intelligently
                when (val refreshState = lazyPagingItems.loadState.refresh) {

                    // 1. Initial Load (Show Shimmer)
                    is LoadState.Loading -> {
                        PromptShimmerList()
                    }

                    // 2. Error State
                    is LoadState.Error -> {
                        SearchEmptyState(
                            message = "System error: ${refreshState.error.message}\nTap to retry."
                        )
                    }

                    // 3. Success / Not Loading
                    is LoadState.NotLoading -> {
                        if (lazyPagingItems.itemCount == 0 && query.isNotEmpty()) {
                            SearchEmptyState(message = "Caught a space-fish, but zero matches..")
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(
                                    count = lazyPagingItems.itemCount,
                                    key = lazyPagingItems.itemKey { it.id }
                                ) { index ->
                                    val prompt = lazyPagingItems[index]
                                    if (prompt != null) {
                                        PromptCard(
                                            prompt = prompt.toUi(),
                                            onCardClick = { onPromptClick(prompt.id) }
                                        )
                                    }
                                }

                                // 4. Pagination Loading (Appending at the bottom)
                                if (lazyPagingItems.loadState.append is LoadState.Loading) {
                                    item {
                                        Box(
                                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                color = Color(0xFFD55900),
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CosmicTopSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit, // Added back navigation callback
    modifier: Modifier = Modifier
) {
    Surface(
        shadowElevation = 8.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        // A Row to act as the Top App Bar layout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp)
                .windowInsetsPadding(WindowInsets.statusBars), // Ensures it stays below the system status bar
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 1. Back Arrow
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Go back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            // 2. Borderless Search Field
            TextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        "Search Galaxies...",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent, // Removes the bottom line
                    unfocusedIndicatorColor = Color.Transparent, // Removes the bottom line
                    cursorColor = Color(0xFFD55900),
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                singleLine = true
            )

            // 3. Clear Text Button (Only shows when there is text)
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Rounded.Clear,
                        contentDescription = "Clear search",
                        tint = Color(0xFFD55900).copy(alpha = 0.6f)
                    )
                }
            } else {
                // Invisible spacer to keep the text field perfectly balanced when the clear icon is hidden
                Spacer(modifier = Modifier.width(48.dp))
            }
        }
    }
}

@Composable
fun PromptShimmerList(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(6) { // Show 6 dummy items
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp) // Approximate height of your PromptCard
                    .cosmicShimmer()
            )
        }
    }
}

@Composable
fun SearchEmptyState(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        CosmicLottieLoader(
            file = "files/astronaut_fish.lottie",
            modifier = Modifier.size(260.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = message,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}