package ai.achaialabs.helios.heliosApp.ui.share.manager

import ai.achaialabs.helios.heliosApp.utils.Haptics
import ai.achaialabs.helios.heliosApp.utils.appContext
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream


actual class ShareManager actual constructor() {

    actual suspend fun sharePrompt(
        imageBitmap: ImageBitmap
    ) {

        val bitmap: Bitmap =
            imageBitmap.asAndroidBitmap()

        val file = File(
            appContext.cacheDir,
            "shared_prompt.png"
        )

        FileOutputStream(file).use {

            bitmap.compress(
                Bitmap.CompressFormat.PNG,
                100,
                it
            )
        }

        val uri = FileProvider.getUriForFile(
            appContext,
            "${appContext.packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {

            type = "image/png"

            putExtra(
                Intent.EXTRA_STREAM,
                uri
            )
            putExtra(
                Intent.EXTRA_TEXT,
                """
               ✨ Shared using Helios AI Prompt Galaxy

                Discover thousands of premium AI prompts.
                https://play.google.com/store/apps/details?id=ai.achaialabs.helios
                """.trimIndent()
            )

            addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }

        appContext.startActivity(

            Intent.createChooser(
                intent,
                "Share Prompt"
            ).apply {

                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }
}