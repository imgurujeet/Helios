package ai.achaialabs.helios.heliosApp.ui.profile

import ai.achaialabs.helios.heliosApp.app.MainViewModel
import ai.achaialabs.helios.heliosApp.firebase.fcm.PushNotificationService
import ai.achaialabs.helios.heliosApp.ui.components.LicensesScreen
import ai.achaialabs.helios.heliosApp.ui.model.UserUi
import ai.achaialabs.helios.heliosApp.ui.navigation.ChromeState
import ai.achaialabs.helios.heliosApp.ui.navigation.Favourite
import ai.achaialabs.helios.heliosApp.ui.profile.components.FeedbackDialog
import ai.achaialabs.helios.heliosApp.ui.profile.components.HeliosDialog
import ai.achaialabs.helios.heliosApp.ui.profile.components.LogoutDialog
import ai.achaialabs.helios.heliosApp.ui.profile.components.ProCard
import ai.achaialabs.helios.heliosApp.ui.profile.components.ProfileCard
import ai.achaialabs.helios.heliosApp.ui.profile.components.RequestPromptDialog
import ai.achaialabs.helios.heliosApp.ui.profile.components.SettingsItem
import ai.achaialabs.helios.heliosApp.ui.profile.components.ThemeSelectionDialog
import ai.achaialabs.helios.heliosApp.utils.ObserveScroll
import ai.achaialabs.helios.heliosApp.utils.getAppVersion
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Feedback
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Palette
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalUriHandler
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel


val heliosOrange = Color(0xF0D55900)
val heliosGold = Color(0xFFF59E0B)

enum class ProfileDialogType {
    NONE, THEME, REQUEST_PROMPT, LOGOUT,FEEDBACK,LICENSES
}
@Composable
fun ProfileScreen(
    chromeState: ChromeState,
    viewModel: ProfileViewModel = koinViewModel(),
    mainViewModel: MainViewModel = koinInject(),
    onUpgradeClick: () -> Unit,
    onLogout: () -> Unit,
    onRequestNotificationPermission: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var activeDialog by remember { mutableStateOf(ProfileDialogType.NONE) }
    val uriHandler = LocalUriHandler.current
    val pushNotificationService: PushNotificationService = koinInject()
    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior()
   // val listState = rememberLazyListState()
    val scrollState= rememberScrollState()
    ObserveScroll(
        scrollState = scrollState,
        chromeState = chromeState
    )
    val userThemePreference by mainViewModel.isDarkTheme.collectAsState()
    val navigationStyle by mainViewModel.navigationStyle.collectAsState()
    val systemTheme = isSystemInDarkTheme()

    val isDarkMode = userThemePreference ?: systemTheme
    val isPro by viewModel.isPremium.collectAsStateWithLifecycle()

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
                currentNavigationStyle = navigationStyle,
                onDismiss = {
                    activeDialog = ProfileDialogType.NONE
                },
                onThemeSelected = { selectedDark ->
                    mainViewModel.setDarkTheme(selectedDark)
                },
                onNavigationStyleSelected = { style ->
                    mainViewModel.setNavigationStyle(style)
                }
            )
        }
        ProfileDialogType.REQUEST_PROMPT -> {

            RequestPromptDialog(
                onDismiss = { activeDialog = ProfileDialogType.NONE },
                onSubmit = { name, message ->
                    activeDialog = ProfileDialogType.NONE
                    val uri = viewModel.getEmailSupportData(
                        type = ProfileViewModel.SupportType.REQUEST_PROMPT,
                        subject = "Prompt Request from $name",
                        body = message
                    )
                    uriHandler.openUri(uri)
                }
            )
        }

        ProfileDialogType.FEEDBACK -> {
            val uriHandler = LocalUriHandler.current
            FeedbackDialog(
                onDismiss = { activeDialog = ProfileDialogType.NONE },
                onSubmit = { category, comment ->
                    activeDialog = ProfileDialogType.NONE
                    val uri = viewModel.getEmailSupportData(
                        type = ProfileViewModel.SupportType.FEEDBACK,
                        subject = "Feedback: $category",
                        body = comment
                    )
                    uriHandler.openUri(uri)
                }
            )
        }

        ProfileDialogType.LICENSES -> {
            HeliosDialog(
                title = "Licenses & Credits",
                onDismiss = { activeDialog = ProfileDialogType.NONE },
            ) {
                LicensesScreen(onDismiss = { activeDialog = ProfileDialogType.NONE })
            }
        }
        ProfileDialogType.NONE -> {}
    }
    var notificationsEnabled by remember {
        mutableStateOf(pushNotificationService.isPermissionGranted())
    }
    notificationsEnabled = pushNotificationService.isPermissionGranted()

    Scaffold(
        modifier = Modifier.nestedScroll(
            scrollBehavior.nestedScrollConnection
        ),
        topBar = {


            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                actions = {
                    IconButton(
                        onClick =  {
                            activeDialog = ProfileDialogType.LOGOUT
                        }
                    ) {
                        Icon(
                            Icons.Rounded.Logout,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )

        }

    ) { paddingValues ->
        ProfileScreenContent(
            user = uiState.user,
            isPro = isPro,
            scrollState = scrollState ,
            onThemeClick = { activeDialog = ProfileDialogType.THEME },
            onNotificationClick = {
                if (!notificationsEnabled) {
                    onRequestNotificationPermission?.invoke()
                }
            },
            onRequestPromptClick = {
                activeDialog = ProfileDialogType.REQUEST_PROMPT
            },
            onVideoGalaxyClick = {

            },
            onRateClick = {},
            onHelpClick = {
                activeDialog = ProfileDialogType.FEEDBACK
            },
            onTermsClick = {
                uriHandler.openUri("https://heliosai.achaialabs.tech/terms")
            },
            onPrivacyClick = {
                uriHandler.openUri("https://heliosai.achaialabs.tech/privacy")
            },
            onAboutClick = {
                activeDialog = ProfileDialogType.LICENSES
            },
            onDeveloperClick = {
                val uri = viewModel.getDeveloperInquiryUri()
                uriHandler.openUri(uri)
            },
            onUpgradeClick = {
                onUpgradeClick()
            },
            onDeleteAccountClick = {
                uriHandler.openUri("https://heliosai.achaialabs.tech/data-safety")
            },
            padding = paddingValues,
            isNotificationEnabled = notificationsEnabled
        )
    }



}


@Composable
fun ProfileScreenContent(
    user: UserUi? = null,
    isPro: Boolean,
    isNotificationEnabled: Boolean,
    onThemeClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onRequestPromptClick: () -> Unit,
    onVideoGalaxyClick: () -> Unit,
    onRateClick: () -> Unit,
    onHelpClick: () -> Unit,
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onAboutClick: () -> Unit,
    onDeveloperClick: () -> Unit,
    onUpgradeClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    padding: PaddingValues,
    scrollState : ScrollState = rememberScrollState(),
) {


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(padding)
            .padding(horizontal = 18.dp),
    ) {


        // Profile Card
        ProfileCard(
            name = user?.name ?: "Nova",
            email = user?.email?:"space for email-id",
            imageUrl = user?.avatarUrl,
            isPro = user?.isPro ?: false
        )
        Spacer(modifier = Modifier.height(22.dp))

        // Pro Card
        if (!isPro){
            ProCard(
                onUpgradeClick = onUpgradeClick
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Settings Section
        ProfileSectionTitle("Preferences")

        Spacer(modifier = Modifier.height(12.dp))

        SettingsItem(
            icon = Icons.Rounded.Palette,
            title = "Appearance",
            subtitle = "Theme, navigation and personalization",
            onClick = {
                onThemeClick()
            }
        )


        SettingsItem(
            icon = Icons.Rounded.Notifications,
            title = "Notifications",
            subtitle = if (isNotificationEnabled) {
                "You're all set for updates."
            } else {
                "Never miss new updates."
            },
            onClick = {
                onNotificationClick()
            }
        )

        SettingsItem(
            icon = Icons.Rounded.Explore,
            title = "Request Prompt",
            subtitle = "Suggest prompts for the community",
            onClick = {
                onRequestPromptClick()
            }
        )

//        SettingsItem(
//            icon = Icons.Rounded.VideoLibrary,
//            title = "Video Galaxy",
//            subtitle = "Explore cinematic AI video prompts",
//            onClick = {
//                onVideoGalaxyClick()
//            }
//        )

        Spacer(modifier = Modifier.height(22.dp))

        // Social Section
        ProfileSectionTitle("Community")

        Spacer(modifier = Modifier.height(12.dp))

        SettingsItem(
            icon = Icons.Rounded.Star,
            title = "Rate Helios",
            subtitle = "Support us on the Play Store",
            onClick = {
                onRateClick()
            }
        )

        SettingsItem(
            icon = Icons.Rounded.Feedback,
            title = "Help & Feedback",
            subtitle = "Report bugs or share ideas",
            onClick = {
                onHelpClick()
            }
        )

        SettingsItem(
            icon = Icons.Rounded.Description,
            title = "Terms of Use",
            subtitle = "Read our terms and conditions",
            onClick = {
                onTermsClick()
            }
        )

        SettingsItem(
            icon = Icons.Rounded.PrivacyTip,
            title = "Privacy Policy",
            subtitle = "Learn how we handle your data",
            onClick = {
                onPrivacyClick()
            }
        )

        SettingsItem(
            icon = Icons.Rounded.Info,
            title = "About",
            subtitle = "Version, credits and libraries",
            onClick = {
                onAboutClick()
            }
        )
        SettingsItem(
            icon = Icons.Rounded.DeleteForever,
            title = "Delete Account",
            subtitle = "Contact us to remove your account and data",
            onClick = {
                onDeleteAccountClick()
            }
        )

        SettingsItem(
            icon = Icons.Rounded.Code,
            title = "Looking for a Developer?",
            subtitle = "Build your next idea with Achaia Labs",
            onClick = {
                onDeveloperClick()
            }
        )

        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "version: ${getAppVersion()}",
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 1.6.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)

            )

        }




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
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
    )
}


