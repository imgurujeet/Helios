package ai.achaialabs.helios.heliosApp.ui.media.player

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun PlatformVideoPlayer(
    url: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = false,
    isPreview: Boolean = false,
    onReady: () -> Unit = {}
)