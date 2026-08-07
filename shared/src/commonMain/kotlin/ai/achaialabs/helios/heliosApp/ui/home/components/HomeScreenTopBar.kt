package ai.achaialabs.helios.heliosApp.ui.home.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.KingBed
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.painterResource
import helios.shared.generated.resources.Res
import helios.shared.generated.resources.crown
import helios.shared.generated.resources.ic_astrnt
import helios.shared.generated.resources.ic_profile
import helios.shared.generated.resources.ic_star
import helios.shared.generated.resources.ic_telescope

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeTopBar(
    userName: String? = "Nova",
    profileImageUrl: String? = null,
    onProfileClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onProIconClick: () -> Unit = {},
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

    val cookieShape = remember {
        MaterialShapes.Cookie9Sided
    }

    val infiniteTransition = rememberInfiniteTransition(label = "")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 12000, // 12 seconds
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background
        ),
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {

                // LEFT
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Box(
                        modifier = Modifier
                           // .padding(start = 12.dp)
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

                    Box(
                        modifier = Modifier.size(30.dp).clickable{
                           if(!isPro) onProIconClick()
                        },
                        contentAlignment = Alignment.Center
                    ) {

                        Box(
                            modifier = Modifier.clickable {
                                if (!isPro) {
                                    onProIconClick()
                                }
                            }
                                .matchParentSize()
                                .graphicsLayer {
                                    rotationZ = rotation
                                }
                                .clip(cookieShape.toShape())
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(0.7f)
                                )
                        )

                        Icon(
                            painter = painterResource(Res.drawable.crown),
                            contentDescription = null,
                            tint = if (isPro)
                                Color(0xFFF59E0B)
                            else
                                MaterialTheme.colorScheme.onBackground.copy(0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // PERFECT CENTER TITLE
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Hi,",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = accent
                    )

                    Spacer(Modifier.width(6.dp))

                    Text(
                        text = firstName ?: "Explorer",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.3.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // RIGHT
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        //.padding(end = 10.dp)
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
                        .clickable { onSearchClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        navigationIcon = {},
        actions = {},
        scrollBehavior = scrollBehavior
    )
}