package ai.achaialabs.promptr.promptrApp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.DotLottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import promptr.shared.generated.resources.Res

@Composable
fun CosmicLottieLoader(
    modifier: Modifier = Modifier
) {

    val composition by rememberLottieComposition {

        LottieCompositionSpec.DotLottie(
            Res.readBytes("files/planet_animation.lottie")
        )
    }

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = Compottie.IterateForever
    )

    Image(
        painter = rememberLottiePainter(
            composition = composition,
            progress = { progress }
        ),
        contentDescription = null,
        modifier = modifier.size(260.dp)
    )
}