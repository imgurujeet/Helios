package ai.achaialabs.helios.heliosApp.ui.home.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.painterResource
import helios.shared.generated.resources.Res
import helios.shared.generated.resources.ic_profile
import helios.shared.generated.resources.ic_telescope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    userName: String? = "Nova",
    profileImageUrl: String? = null,
    onProfileClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    isPro: Boolean = false,
    scrollBehavior: TopAppBarScrollBehavior
) {

    val accent = Color(0xF0D55900)

    val firstName = remember(userName) {
        userName
            ?.trim()
            ?.split(" ")
            ?.firstOrNull()
    }

    TopAppBar(

        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background
        ),

        // LEFT ICON
        navigationIcon = {

            Box(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(44.dp)
                    .background(
                        brush = if (isPro) {
                            Brush.sweepGradient(
                                listOf(
                                    Color(0xFFFFD54F),
                                    Color(0xF0EC6400),
                                    Color(0xFFFFE082),
                                    Color(0xF0D55900)
                                )
                            )
                        } else {
                            SolidColor(Color.Transparent)
                        },
                        shape = CircleShape
                    )
                    .padding(if (isPro) 2.dp else 0.dp)
                    .clip(CircleShape)
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = profileImageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(Res.drawable.ic_profile),
                    error = painterResource(Res.drawable.ic_profile),
                    fallback = painterResource(Res.drawable.ic_profile)
                )
            }
        },

        // CENTER TITLE
        title = {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Hi,",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = accent
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = firstName ?: "Explorer",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.3.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },

        // RIGHT SEARCH
        actions = {

            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                accent.copy(alpha = 0.18f),
                                Color.Transparent
                            )
                        )
                    )
                    .border(
                        1.dp,
                        accent.copy(alpha = 0.2f),
                        CircleShape
                    )
                    .clickable {
                        onSearchClick()
                    },
                contentAlignment = Alignment.Center
            )  {

                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground.copy(
                        alpha = 0.85f
                    ),
                    modifier = Modifier.size(24.dp)
                )
            }
        },

        scrollBehavior = scrollBehavior
    )
}