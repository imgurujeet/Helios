package ai.achaialabs.helios.heliosApp.ui.share.manager

import androidx.compose.ui.graphics.ImageBitmap


expect class ShareManager() {

    suspend fun sharePrompt(
        imageBitmap: ImageBitmap
    )
}