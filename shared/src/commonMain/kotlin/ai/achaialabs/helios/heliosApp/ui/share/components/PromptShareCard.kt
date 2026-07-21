package ai.achaialabs.helios.heliosApp.ui.share.components

import ai.achaialabs.helios.heliosApp.ui.share.model.SharePromptUi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

@Composable
fun PromptShareCard(
    data: SharePromptUi,
    onImageLoaded: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // .aspectRatio(1f) guarantees the card is ALWAYS a perfect square,
    // no matter the screen size.
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF12141D),
                        Color(0xFF08090D)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // =========================
            // CENTER: The Main Image
            // =========================
            AsyncImage(
                model = data.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                onSuccess = { onImageLoaded() },
                modifier = Modifier
                    // Takes up exactly 75% of the card, leaving beautiful empty space around it
                    .fillMaxWidth(0.75f)
                    // Keeps the inner image a perfect square as well
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(24.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // =========================
            // BOTTOM: Minimal Typography
            // =========================
            // We use the same 0.75f width so the text perfectly aligns
            // with the left and right edges of the image above it.
            Row(
                modifier = Modifier.fillMaxWidth(0.75f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Title and Brand
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = data.promptTitle,
                        color = Color.White,
                        fontSize = 20.sp, // Adjusted for phone screen
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "HELIOS AI",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp, // Adjusted for phone screen
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 4.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Right: Minimal Icon/Logo inside a subtle circle
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.08f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome, // Replace with your logo
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}