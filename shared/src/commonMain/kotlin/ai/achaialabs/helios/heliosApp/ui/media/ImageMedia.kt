package ai.achaialabs.helios.heliosApp.ui.media

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlin.math.absoluteValue

@Composable
fun ImageMedia(
    url: String,
    modifier: Modifier = Modifier
) {
    var loaded by remember { mutableStateOf(false) }
    val placeholderHeight = remember(url) {
        when (url.hashCode().absoluteValue % 4) {
            0 -> 180.dp
            1 -> 240.dp
            2 -> 300.dp
            else -> 360.dp
        }
    }

    AsyncImage(
        model = url,
        contentDescription = null,
        modifier = modifier.then(
            if (!loaded) Modifier.height(placeholderHeight)
            else Modifier
        ),
        contentScale = ContentScale.Crop,
        onSuccess = {
            loaded = true
        }
    )
}