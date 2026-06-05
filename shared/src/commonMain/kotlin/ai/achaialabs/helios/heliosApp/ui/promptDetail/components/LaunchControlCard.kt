package ai.achaialabs.helios.heliosApp.ui.promptDetail.components

import ai.achaialabs.helios.heliosApp.domain.model.Tool
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

@Composable
fun LaunchControlCard(
    recommendedToolId: String,
    tools: List<Tool>,
    isLoading: Boolean,
    onToolClick: (Tool) -> Unit = {}
) {
    // Assuming CosmicGlassCard handles its own background transparency appropriately
    val sortedTools = remember(tools, recommendedToolId) {
        tools.sortedByDescending { it.id == recommendedToolId }
    }
    val listState = rememberLazyListState()

    LaunchedEffect(recommendedToolId) {
        listState.scrollToItem(0)
    }
    CosmicGlassCard {
        // --- HEADER ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Tiny glowing online indicator using the primary theme color
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(color = Color(0xF0D55900))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "UPLINKS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFFF59E0B)
                )
            }

            // Sub-text showing status (onSurfaceVariant adjusts to dark/light automatically)
            Text(
                text = if (isLoading) "SCANNING..." else "",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- TOOLS ROW & COPY BUTTON ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // LEFT SIDE: LOADING OR TOOLS
            Box(modifier = Modifier.weight(1f).height(64.dp)) {
                if (isLoading && tools.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else {
                    LazyRow(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(end = 8.dp) // Buffer before copy button
                    ) {
                        items(sortedTools, key = { it.id }) { currentTool ->
                            CosmicToolCard(
                                tool = currentTool,
                                isSuggested = currentTool.id == recommendedToolId,
                                {onToolClick(currentTool)}
                            )
                        }
                    }
                }
            }

        }
    }
}

// --- THE UPGRADED TOOL CARD ---
@Composable
fun CosmicToolCard(
    tool: Tool,
    isSuggested: Boolean,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .height(64.dp)
            .animateContentSize()
            .then(
                if (isSuggested) Modifier.widthIn(min = 140.dp).padding(end = 4.dp)
                else Modifier.width(64.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            // Subtle fill background based on selection state
            .background(
                if (isSuggested) MaterialTheme.colorScheme.background.copy(alpha = 0.1f)
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f)
            )
            // Gradient border utilizing primary for suggested, outlineVariant for unselected
            .border(
                width = 1.dp,
                brush = if (isSuggested) {
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFF59E0B).copy(alpha = 0.8f),
                            Color(0xFFF59E0B).copy(alpha = 0.2f)
                        )
                    )
                } else {
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.outlineVariant,
                            Color.Transparent
                        )
                    )
                },
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = if (isSuggested) 16.dp else 0.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isSuggested) Arrangement.Start else Arrangement.Center
    ) {

        // The Tool Icon
        AsyncImage(
            model = tool.iconUrl,
            contentDescription = tool.name,
            modifier = Modifier.size(28.dp)
        )

        // The Explicit Text
        if (isSuggested) {
            Spacer(modifier = Modifier.width(12.dp))

            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "OPTIMAL",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 8.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = Color(0xF0D55900)
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = tool.name,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    ),
                    // Changes to black text on light mode, white on dark mode automatically
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}