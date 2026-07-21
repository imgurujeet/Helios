package ai.achaialabs.helios.heliosApp.ad

import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.nativead.NativeAdView
import org.koin.compose.koinInject


@Composable
actual fun NativeAdCard(
    modifier: Modifier
) {

    val adManager: AdManager =
        koinInject()

    val nativeAd =
        (adManager as? AndroidAdManager)
            ?.getNativeAd()

    if (nativeAd == null) return

    val backgroundColor =
        MaterialTheme.colorScheme.surface

    val headlineColor =
        MaterialTheme.colorScheme.onSurface

    val bodyColor =
        MaterialTheme.colorScheme.onSurface

    val buttonContainerColor = Color(0xF0D55900)


    val buttonTextColor = MaterialTheme.colorScheme.onSurface

    AndroidView(
        modifier = modifier,

        factory = { context ->

            val nativeAdView =
                NativeAdView(context)

            val container =
                LinearLayout(context).apply {

                    orientation =
                        LinearLayout.VERTICAL

                    setPadding(
                        40,
                        40,
                        40,
                        40
                    )

                    background =
                        GradientDrawable().apply {

                            cornerRadius = 36f

                            setColor(
                                backgroundColor.toArgb()
                            )
                        }
                }

            val sponsoredLabel =
                TextView(context).apply {

                    text = "Sponsored"

                    textSize = 11f

                    setTextColor(
                        bodyColor.toArgb()
                    )
                }

            val headlineView =
                TextView(context).apply {

                    textSize = 18f

                    setTypeface(
                        typeface,
                        Typeface.BOLD
                    )

                    setTextColor(
                        headlineColor.toArgb()
                    )
                }

            val bodyView =
                TextView(context).apply {

                    textSize = 14f

                    setTextColor(
                        bodyColor.toArgb()
                    )
                }

            val ctaButton =
                Button(context).apply {

                    setTextColor(
                        buttonTextColor.toArgb()
                    )

                    background =
                        GradientDrawable().apply {

                            cornerRadius = 100f

                            setColor(
                                buttonContainerColor.toArgb()
                            )
                        }
                }

            container.addView(
                sponsoredLabel
            )

            container.addView(
                headlineView
            )

            container.addView(
                bodyView
            )

            container.addView(
                ctaButton
            )

            nativeAdView.addView(container)

            nativeAdView.headlineView =
                headlineView

            nativeAdView.bodyView =
                bodyView

            nativeAdView.callToActionView =
                ctaButton

            headlineView.text =
                nativeAd.headline

            bodyView.text =
                nativeAd.body

            ctaButton.text =
                nativeAd.callToAction

            nativeAdView.setNativeAd(
                nativeAd
            )

            nativeAdView
        }
    )
}