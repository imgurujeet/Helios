package ai.achaialabs.promptr.promptrApp.ui.profile

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Feedback
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import promptr.shared.generated.resources.Res
import promptr.shared.generated.resources.ic_google

@Composable
fun ProfileScreen(
    onLogout: () -> Unit
){
    Scaffold(
        containerColor = Color(0xFF05070B)
    ) { paddingValues ->
        ProfileScreenContent(
            onLogout = onLogout,
            padding = paddingValues
        )
    }

}


@Composable
fun ProfileScreenContent(
    onLogout: () -> Unit,
    padding: PaddingValues
) {

    val scrollState = rememberScrollState()



        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF05070B))
        ) {

            // Cosmic Background Glow
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x22F59E0B),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(padding)
                    .padding(horizontal = 18.dp),
            ) {

                Spacer(modifier = Modifier.height(18.dp))

                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )

                    IconButton(
                        onClick = onLogout
                    ) {
                        Icon(
                            Icons.Rounded.Logout,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Profile Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF101720)
                    ),
                    shape = RoundedCornerShape(30.dp)
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFFF59E0B),
                                            Color(0xFFFFC857)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                Icons.Rounded.Person,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(42.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "Welcome to Helios",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Prompts From Across the Galaxy",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.6f)
                        )

                        Spacer(modifier = Modifier.height(22.dp))

                        Button(
                            onClick = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(100.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            )
                        ) {

                            Icon(
                                painter = painterResource(Res.drawable.ic_google),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = "Continue with Google",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Pro Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF0F2A30)
                    )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(Color(0x22F59E0B)),
                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = "Upgrade to Helios Pro",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Unlock premium prompts and exclusive visuals.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.65f)
                            )
                        }

                        Icon(
                            Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Settings Section
                ProfileSectionTitle("Preferences")

                Spacer(modifier = Modifier.height(12.dp))

                SettingsItem(
                    icon = Icons.Rounded.DarkMode,
                    title = "Theme",
                    subtitle = "Switch between dark and light mode"
                )

                SettingsItem(
                    icon = Icons.Rounded.Bookmark,
                    title = "Saved Prompts",
                    subtitle = "Your collected inspirations"
                )

                SettingsItem(
                    icon = Icons.Rounded.Explore,
                    title = "Request Prompt",
                    subtitle = "Suggest prompts for the community"
                )

                SettingsItem(
                    icon = Icons.Rounded.VideoLibrary,
                    title = "Video Galaxy",
                    subtitle = "Explore cinematic AI video prompts"
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Social Section
                ProfileSectionTitle("Community")

                Spacer(modifier = Modifier.height(12.dp))

                SettingsItem(
                    icon = Icons.Rounded.Star,
                    title = "Rate Helios",
                    subtitle = "Support us on the Play Store"
                )

                SettingsItem(
                    icon = Icons.Rounded.Feedback,
                    title = "Help & Feedback",
                    subtitle = "Report bugs or share ideas"
                )

                SettingsItem(
                    icon = Icons.Rounded.Science,
                    title = "Join Our Beta",
                    subtitle = "Test upcoming Helios features"
                )

                SettingsItem(
                    icon = Icons.Rounded.Description,
                    title = "Terms of Use",
                    subtitle = "Read our terms and conditions"
                )

                SettingsItem(
                    icon = Icons.Rounded.PrivacyTip,
                    title = "Privacy Policy",
                    subtitle = "Learn how we handle your data"
                )

                SettingsItem(
                    icon = Icons.Rounded.Info,
                    title = "About",
                    subtitle = "Version, credits and libraries"
                )

                SettingsItem(
                    icon = Icons.Rounded.Code,
                    title = "Looking for a Developer?",
                    subtitle = "Build your next idea with AchaiLabs"
                )

                Spacer(modifier = Modifier.height(24.dp))

                // AchaiLabs Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF101720)
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(22.dp)
                    ) {

                        Text(
                            text = "FROM ACHAIALABS",
                            style = MaterialTheme.typography.labelMedium.copy(
                                letterSpacing = 2.sp
                            ),
                            color = Color.White.copy(alpha = 0.35f)
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "More Apps Coming Soon",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Crafting futuristic creative tools for the next generation.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.6f),
                            lineHeight = 22.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }

}


@Composable
fun ProfileSectionTitle(
    title: String
) {

    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(
            letterSpacing = 1.6.sp,
            fontWeight = FontWeight.SemiBold
        ),
        color = Color.White.copy(alpha = 0.4f)
    )
}


@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color(0x11F59E0B)),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFF59E0B)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.55f)
            )
        }

        Icon(
            Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.3f)
        )
    }
}