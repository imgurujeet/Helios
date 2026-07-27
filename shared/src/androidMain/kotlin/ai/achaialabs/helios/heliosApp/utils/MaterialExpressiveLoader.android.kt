package ai.achaialabs.helios.heliosApp.utils

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
actual fun MaterialExpressiveLoader(modifier: Modifier) {
    CircularWavyProgressIndicator(
        modifier = modifier.size(56.dp),
        color = MaterialTheme.colorScheme.primary,
    )

}