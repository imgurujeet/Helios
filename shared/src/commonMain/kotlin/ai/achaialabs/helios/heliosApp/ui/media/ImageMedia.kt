package ai.achaialabs.helios.heliosApp.ui.media

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
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
    aspectRatio: Float?,
    modifier: Modifier = Modifier
) {
    val ratio = aspectRatio
        ?.takeIf { it > 0f }
        ?: 1f

    AsyncImage(
        model = url,
        contentDescription = null,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(ratio),
        contentScale = ContentScale.Crop
    )
}