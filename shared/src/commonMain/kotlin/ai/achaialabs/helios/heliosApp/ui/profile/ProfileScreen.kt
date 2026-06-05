package ai.achaialabs.helios.heliosApp.ui.profile

import ai.achaialabs.helios.heliosApp.ui.model.UserUi
import ai.achaialabs.helios.heliosApp.ui.profile.components.LogoutDialog
import ai.achaialabs.helios.heliosApp.ui.profile.components.ProCard
import ai.achaialabs.helios.heliosApp.ui.profile.components.ProfileCard
import ai.achaialabs.helios.heliosApp.ui.profile.components.RequestPromptDialog
import ai.achaialabs.helios.heliosApp.ui.profile.components.SettingsItem
import ai.achaialabs.helios.heliosApp.ui.profile.components.ThemeSelectionDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.painterResource
import helios.shared.generated.resources.Res
import helios.shared.generated.resources.ic_google
import helios.shared.generated.resources.ic_star_orbit
import org.koin.compose.viewmodel.koinViewModel


val heliosOrange = Color(0xF0D55900)
val heliosGold = Color(0xFFF59E0B)

enum class ProfileDialogType {
    NONE, THEME, REQUEST_PROMPT, LOGOUT
}
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var activeDialog by remember { mutableStateOf(ProfileDialogType.NONE) }
    val isDarkMode = isSystemInDarkTheme()

    when (activeDialog) {
        ProfileDialogType.LOGOUT -> {
            LogoutDialog(
                onDismiss = { activeDialog = ProfileDialogType.NONE },
                onConfirm = {
                    activeDialog = ProfileDialogType.NONE
                    viewModel.logout() // 1. Clear session
                    onLogout()         // 2. Navigate away
                }
            )
        }
        ProfileDialogType.THEME -> {
            ThemeSelectionDialog(
                currentIsDark = isDarkMode,
                onDismiss = { activeDialog = ProfileDialogType.NONE },
                onThemeSelected = { selectedDark ->
                   // isDarkMode = selectedDark
                    // Trigger any global theme changes here
                }
            )
        }
        ProfileDialogType.REQUEST_PROMPT -> {
            RequestPromptDialog(
                onDismiss = { activeDialog = ProfileDialogType.NONE },
                onSubmit = { name, message ->
                    // Handle opening your email client here
                }
            )
        }
        ProfileDialogType.NONE -> {}
    }

    Scaffold(

    ) { paddingValues ->
        ProfileScreenContent(
            user = uiState.user,
            onLogout = { activeDialog = ProfileDialogType.LOGOUT },
            onThemeClick = { activeDialog = ProfileDialogType.THEME },
            onSavedClick = {},
            onRequestPromptClick = {},
            onVideoGalaxyClick = {},
            onRateClick = {},
            onHelpClick = {},
            onTermsClick = {},
            onPrivacyClick = {},
            onAboutClick = {},
            onDeveloperClick = {},
            onUpgradeClick = {},
            padding = paddingValues
        )
    }



}


@Composable
fun ProfileScreenContent(
    user: UserUi? = null,
    onLogout: () -> Unit,
    onThemeClick: () -> Unit,
    onSavedClick: () -> Unit,
    onRequestPromptClick: () -> Unit,
    onVideoGalaxyClick: () -> Unit,
    onRateClick: () -> Unit,
    onHelpClick: () -> Unit,
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onAboutClick: () -> Unit,
    onDeveloperClick: () -> Unit,
    onUpgradeClick: () -> Unit,
    padding: PaddingValues
) {

    val scrollState = rememberScrollState()

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
                color = MaterialTheme.colorScheme.onBackground
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
        ProfileCard(
            name = user?.name ?: "Nova",
            email = user?.email?:"space for email-id",
            imageUrl = user?.avatarUrl,
            isPro = user?.isPro ?: false
        )
        Spacer(modifier = Modifier.height(22.dp))

        // Pro Card
        if (user?.isPro == false){
            ProCard()
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Settings Section
        ProfileSectionTitle("Preferences")

        Spacer(modifier = Modifier.height(12.dp))

        SettingsItem(
            icon = Icons.Rounded.DarkMode,
            title = "Theme",
            subtitle = "Switch between dark and light mode",
            onClick = {}
        )

        SettingsItem(
            icon = Icons.Rounded.Bookmark,
            title = "Saved Prompts",
            subtitle = "Your collected inspirations",
            onClick = {}
        )

        SettingsItem(
            icon = Icons.Rounded.Explore,
            title = "Request Prompt",
            subtitle = "Suggest prompts for the community",
            onClick = {}
        )

        SettingsItem(
            icon = Icons.Rounded.VideoLibrary,
            title = "Video Galaxy",
            subtitle = "Explore cinematic AI video prompts",
            onClick = {}
        )

        Spacer(modifier = Modifier.height(22.dp))

        // Social Section
        ProfileSectionTitle("Community")

        Spacer(modifier = Modifier.height(12.dp))

        SettingsItem(
            icon = Icons.Rounded.Star,
            title = "Rate Helios",
            subtitle = "Support us on the Play Store",
            onClick = {}
        )

        SettingsItem(
            icon = Icons.Rounded.Feedback,
            title = "Help & Feedback",
            subtitle = "Report bugs or share ideas",
            onClick = {}
        )

        SettingsItem(
            icon = Icons.Rounded.Description,
            title = "Terms of Use",
            subtitle = "Read our terms and conditions",
            onClick = {}
        )

        SettingsItem(
            icon = Icons.Rounded.PrivacyTip,
            title = "Privacy Policy",
            subtitle = "Learn how we handle your data",
            onClick = {}
        )

        SettingsItem(
            icon = Icons.Rounded.Info,
            title = "About",
            subtitle = "Version, credits and libraries",
            onClick = {}
        )

        SettingsItem(
            icon = Icons.Rounded.Code,
            title = "Looking for a Developer?",
            subtitle = "Build your next idea with Achaia Labs",
            onClick = {}
        )


        Spacer(modifier = Modifier.height(85.dp))
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


