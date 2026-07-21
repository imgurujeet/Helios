package ai.achaialabs.helios.heliosApp.ui.explore.components


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import helios.shared.generated.resources.Res
import helios.shared.generated.resources.ic_star_orbit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreHeader(
    isPremium: Boolean,
    onUnlockPremiumClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {

    TopAppBar(
        title = {
            Text(
                text = "Constellations",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            )
        },

        actions = {
            if (!isPremium) {
            Row(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFF59E0B))
                    .clickable {
                        onUnlockPremiumClick()
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp),

                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    painter = painterResource(Res.drawable.ic_star_orbit),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = 6.dp)
                )

               Text(
                    text = "Unlock Pro",
                    color = MaterialTheme.colorScheme.surface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
               )
               }
            }
        },

        scrollBehavior = scrollBehavior
    )
}