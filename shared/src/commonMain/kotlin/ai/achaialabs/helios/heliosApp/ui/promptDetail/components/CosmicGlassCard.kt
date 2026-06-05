package ai.achaialabs.helios.heliosApp.ui.promptDetail.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CosmicGlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp, // Kept thin and light
                brush = Brush.linearGradient(
                    // 🚀 LIGHT & TRANSPARENT GLASS BORDER
                    0.0f to Color(0xF0D55900).copy(alpha = 0.6f),   // Strong light catch on the top/left edge
                    0.25f to Color.Transparent,               // Quickly fades to completely transparent
                    0.75f to Color.Transparent,               // Stays totally transparent through the center
                    1.0f to Color(0xF0D55900).copy(alpha = 0.4f)    // Softer light catch on the bottom/right edge
                ),
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}
