package ai.achaialabs.helios.heliosApp.ui.onboarding

import ai.achaialabs.helios.heliosApp.ui.CosmicLottieLoader
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.composeAuth
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import kotlin.random.Random

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = koinViewModel(),
    supabaseClient: SupabaseClient = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()

    val action = supabaseClient.composeAuth.rememberSignInWithGoogle(
        onResult = { result ->
            when (result) {
                is NativeSignInResult.Success -> {
                    viewModel.onNativeSignInSuccess()
                }
                is NativeSignInResult.Error -> {
                    viewModel.onError(result.message)
                }
                is NativeSignInResult.ClosedByUser -> {}
                is NativeSignInResult.NetworkError -> viewModel.onError("Network error")
            }
        },
        fallback = {
            viewModel.onError("Google Sign-In not supported on this platform")
        }
    )

    LoginScreenContent(
        uiState = uiState,
        onGoogleSignIn = { action.startFlow() },
        onPrivacyClick = {  },
        onTermsClick = {  }
    )

    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            onLoginSuccess()
        }
    }




}


@Composable
fun LoginScreenContent(
    uiState: LoginUiState,
    onGoogleSignIn: () -> Unit,
    onPrivacyClick: () -> Unit,
    onTermsClick: () -> Unit
) {

    val infiniteTransition = rememberInfiniteTransition(label = "space")

    val darkTheme = isSystemInDarkTheme()

    val nebulaOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 18000,
                easing = LinearEasing
            )
        ),
        label = "nebula"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (darkTheme) Color(0xFF05070B) else MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            )
    ) {

        // Cosmic Background
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

            // Large soft glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x22F59E0B),
                        Color.Transparent
                    )
                ),
                radius = size.width * 0.55f,
                center = Offset(
                    x = size.width * 0.75f,
                    y = size.height * 0.25f
                )
            )

            // Nebula sweep
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0x10F59E0B),
                        Color.Transparent
                    ),
                    start = Offset(nebulaOffset - 500f, 0f),
                    end = Offset(nebulaOffset, size.height)
                )
            )

            // Stars
            repeat(90) {

                drawCircle(
                    color = Color.White.copy(
                        alpha = Random.nextFloat() * 0.6f
                    ),
                    radius = Random.nextFloat() * 2.2f,
                    center = Offset(
                        x = Random.nextFloat() * size.width,
                        y = Random.nextFloat() * size.height
                    )
                )
            }
        }

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(110.dp))

            // Logo / Animation
            CosmicLottieLoader()

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Helios",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "AI Prompts From Across the Galaxy",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onGoogleSignIn,
                enabled = uiState !is LoginUiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp),
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF59E0B),
                    contentColor = Color.Black
                )
            ) {

                if (uiState is LoginUiState.Loading) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.Black,
                        strokeWidth = 2.4.dp
                    )

                } else {

                    Text(
                        text = "Continue with Google",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "By continuing, you agree to our",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Terms of Use",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.clickable {
                        onTermsClick()
                    }
                )

                Text(
                    text = " & ",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f)
                )

                Text(
                    text = "Privacy Policy",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.clickable {
                        onPrivacyClick()
                    }
                )
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}