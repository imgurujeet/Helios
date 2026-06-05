package ai.achaialabs.helios.heliosApp.ui.profile.components

import ai.achaialabs.helios.heliosApp.ui.profile.heliosGold
import ai.achaialabs.helios.heliosApp.ui.profile.heliosOrange
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import helios.shared.generated.resources.Res
import helios.shared.generated.resources.ic_profile
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

@Composable
@Preview
fun ProfileCard(
    name: String = "Gurujeet Kumar",
    email: String = "gurujeet@gmail.com",
    imageUrl: String? = null,
    isPro: Boolean = true,
    modifier: Modifier = Modifier
) {

    val accent = Color(0xF0D55900)

    val glowBrush = Brush.horizontalGradient(
        colors = listOf(
            accent.copy(alpha = 0.15f),
            Color.Transparent,
            Color.Transparent
        )
    )

    val firstName = remember(name) { name.split(" ").firstOrNull() ?: name }

    // Wrapped in remember so it doesn't recreate the list on every recomposition
    val creatorVibeTexts = remember(firstName) {
        listOf(
            "My creator told me you're his favorite user, $firstName. Don't tell the others.",
            "I scanned the whole database, $firstName. You officially have the highest aura score.",
            "Don't tell my creator, $firstName, but you make this UI look way better than he designed it.",
            "I overheard the dev say he only fixed that last bug because he knew $firstName was going to log in.",
            "The dev spent hours trying to hardcode a compliment cool enough for $firstName. I told him to give up. You're flawless.",
            "The dev was going to put ads here, but I threatened to crash the app if he ruined $firstName's view.",
            "Between you and me, $firstName, I'm the one actually running things around here. The dev just takes the credit.",
            "Just a heads up, $firstName: the dev spent 3 hours centering your profile picture. Please appreciate it.",
            "I’d tell you how much sleep the dev lost building this screen, $firstName, but it would just make us both sad."
        )
    }

    var displayedText by remember { mutableStateOf("") }

    // The infinite looping typewriter effect
    LaunchedEffect(name) {
        while (true) {
            // 1. Shuffle the entire list so the order is a surprise every time
            val shuffledDeck = creatorVibeTexts.shuffled()

            // 2. Go through every single quote one by one
            for (text in shuffledDeck) {
                displayedText = ""

                // Type it out
                for (char in text) {
                    displayedText += char
                    delay(40)
                }

                // Wait for them to read it before moving to the next one in the deck
                delay(4000)
            }
            // 3. Once the 'for' loop finishes, the 'while(true)' restarts,
            // reshuffles the deck, and does it again!
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.96f)
        ),
//        border = BorderStroke(
//            width = 1.dp,
//            brush = Brush.linearGradient(
//                colors = listOf(
//                    heliosGold.copy(alpha = 0.5f),
//                    heliosOrange.copy(alpha = 0.1f),
//                    // Color.Transparent
//                )
//            )
//        )
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.96f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // LEFT: PROFILE IMAGE & BADGE
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(accent.copy(alpha = 0.25f), Color.Transparent)
                                )
                            )
                            .border(
                                2.dp,
                                Brush.linearGradient(
                                    listOf(accent.copy(alpha = 0.5f), accent.copy(alpha = 0.1f))
                                ),
                                CircleShape
                            )
                    ) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(Res.drawable.ic_profile),
                            error = painterResource(Res.drawable.ic_profile),
                            fallback = painterResource(Res.drawable.ic_profile),
                            colorFilter = if (imageUrl.isNullOrEmpty()) {
                                ColorFilter.tint(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                            } else null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    if (isPro) {
                        Box(
                            modifier = Modifier
                                .offset(x = 4.dp, y = 4.dp)
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            Color(0xFFFFD54F),
                                            Color(0xFFF59E0B)
                                        )
                                    )
                                )
                                .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.RocketLaunch,
                                contentDescription = "Pro Member",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // RIGHT: USER DETAILS ONLY
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.2.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }


        // CHAT BUBBLE
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomEnd = 12.dp,
                        bottomStart = 2.dp
                    )
                )
                .background(accent.copy(alpha = 0.08f))
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = displayedText,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontStyle = FontStyle.Italic,
                    lineHeight = 16.sp
                ),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                maxLines = 3,
                softWrap = true,
                overflow = TextOverflow.Ellipsis
            )
        }


    }

}