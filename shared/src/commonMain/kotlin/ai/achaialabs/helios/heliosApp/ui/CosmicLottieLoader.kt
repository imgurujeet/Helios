package ai.achaialabs.helios.heliosApp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.DotLottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import helios.shared.generated.resources.Res


@Composable
fun CosmicLottieLoader(
    file:String = "files/planet_animation.lottie",
    modifier: Modifier = Modifier
) {

    var lottieBytes by remember {
        mutableStateOf<ByteArray?>(null)
    }

    LaunchedEffect(file) {
        lottieBytes = Res.readBytes(file)
    }

    val composition by rememberLottieComposition {

        LottieCompositionSpec.DotLottie(
            lottieBytes ?: ByteArray(0)
        )
    }

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = Compottie.IterateForever
    )

    if (composition != null) {

        Image(
            painter = rememberLottiePainter(
                composition = composition,
                progress = { progress }
            ),
            contentDescription = null,
            modifier = modifier.size(260.dp)
        )
    }
}