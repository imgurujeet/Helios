package ai.achaialabs.helios.heliosApp.ad

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAdView
import org.koin.compose.koinInject

private fun Int.dp(context: Context): Int =
    (this * context.resources.displayMetrics.density).toInt()

@Composable
actual fun NativeAdCard(
    modifier: Modifier
) {

    val adManager: AdManager = koinInject()

    val nativeAd =
        (adManager as? AndroidAdManager)?.getNativeAd()
            ?: return

    val backgroundColor = MaterialTheme.colorScheme.surface
    val headlineColor = MaterialTheme.colorScheme.onSurface
    val bodyColor = MaterialTheme.colorScheme.onSurface

    val buttonBackground = Color(0xF0D55900)
    val buttonText = MaterialTheme.colorScheme.onSurface

    AndroidView(

        modifier = modifier,

        factory = { context ->

            val nativeAdView = NativeAdView(context)

            val container = LinearLayout(context).apply {

                orientation = LinearLayout.VERTICAL

                setPadding(
                    14.dp(context),
                    14.dp(context),
                    14.dp(context),
                    14.dp(context)
                )

                background = GradientDrawable().apply {

                    cornerRadius = 18.dp(context).toFloat()

                    setColor(backgroundColor.toArgb())
                }
            }

            // ----------------------------
            // Header
            // ----------------------------

            val headerRow = LinearLayout(context).apply {

                orientation = LinearLayout.HORIZONTAL

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {

                    bottomMargin = 10.dp(context)
                }
            }

            val iconView = ImageView(context).apply {

                layoutParams = LinearLayout.LayoutParams(
                    42.dp(context),
                    42.dp(context)
                )

                scaleType = ImageView.ScaleType.CENTER_CROP
            }

            iconView.clipToOutline = true
            iconView.background = GradientDrawable().apply {
                cornerRadius = 12.dp(context).toFloat()
            }

            val textColumn = LinearLayout(context).apply {

                orientation = LinearLayout.VERTICAL

                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {

                    leftMargin = 12.dp(context)
                }
            }

            val headlineView = TextView(context).apply {

                textSize = 16f


                setTypeface(typeface, Typeface.BOLD)

                setTextColor(headlineColor.toArgb())
            }
            headlineView.maxLines = 1

            headlineView.ellipsize =
                android.text.TextUtils.TruncateAt.END

            val advertiserView = TextView(context).apply {

                textSize = 12f

                setTextColor(bodyColor.toArgb())
            }

            val sponsoredView = TextView(context).apply {

                text = "Sponsored"

                textSize = 10f

                alpha = 0.65f

                setTextColor(bodyColor.toArgb())
            }

            textColumn.addView(headlineView)
            textColumn.addView(advertiserView)

            headerRow.addView(iconView)
            headerRow.addView(textColumn)
            headerRow.addView(sponsoredView)

            // ----------------------------
            // Media
            // ----------------------------

            val mediaView = MediaView(context).apply {

                clipToOutline = true

                background = GradientDrawable().apply {

                    cornerRadius = 14.dp(context).toFloat()
                }

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    120.dp(context)
                ).apply {

                    bottomMargin = 12.dp(context)
                }
            }

            // ----------------------------
            // Body
            // ----------------------------

            val bodyView = TextView(context).apply {

                textSize = 14f

                maxLines = 2

                setTextColor(bodyColor.toArgb())

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {

                    bottomMargin = 12.dp(context)
                }
            }

            // ----------------------------
            // CTA
            // ----------------------------

            val ctaButton = Button(context).apply {

                isClickable = false
                isFocusable = false

                textSize = 12f

                minimumHeight = 0
                minHeight = 0

                setPadding(
                    18.dp(context),
                    8.dp(context),
                    18.dp(context),
                    8.dp(context)
                )

                setTextColor(buttonText.toArgb())

                background = GradientDrawable().apply {

                    cornerRadius = 100.dp(context).toFloat()

                    setColor(buttonBackground.toArgb())
                }
            }

            container.addView(headerRow)
            container.addView(mediaView)
            container.addView(bodyView)
            container.addView(ctaButton)

            nativeAdView.addView(container)

            nativeAdView.mediaView = mediaView
            nativeAdView.iconView = iconView
            nativeAdView.headlineView = headlineView
            nativeAdView.bodyView = bodyView
            nativeAdView.callToActionView = ctaButton
            nativeAdView.advertiserView = advertiserView

            nativeAdView
        },

        update = { nativeAdView ->

            val media =
                nativeAdView.mediaView as MediaView

            val headline =
                nativeAdView.headlineView as TextView

            val body =
                nativeAdView.bodyView as TextView

            val advertiser =
                nativeAdView.advertiserView as TextView

            val icon =
                nativeAdView.iconView as ImageView

            val cta =
                nativeAdView.callToActionView as Button

            // ----------------------------
            // Required
            // ----------------------------

            headline.text = nativeAd.headline

            // ----------------------------
            // Media
            // ----------------------------

            if (nativeAd.mediaContent != null &&
                nativeAd.mediaContent!!.hasVideoContent() ||
                nativeAd.images.isNotEmpty()
            ) {

                media.visibility = View.VISIBLE

            } else {

                media.visibility = View.GONE
            }

            // ----------------------------
            // Body
            // ----------------------------

            if (!nativeAd.body.isNullOrBlank()) {

                body.text = nativeAd.body

                body.visibility = View.VISIBLE

            } else {

                body.visibility = View.GONE
            }

            // ----------------------------
            // Advertiser
            // ----------------------------

            if (!nativeAd.advertiser.isNullOrBlank()) {

                advertiser.text = nativeAd.advertiser

                advertiser.visibility = View.VISIBLE

            } else {

                advertiser.visibility = View.GONE
            }

            // ----------------------------
            // Icon
            // ----------------------------

            if (nativeAd.icon != null) {

                icon.setImageDrawable(nativeAd.icon!!.drawable)

                icon.visibility = View.VISIBLE

            } else {

                icon.visibility = View.GONE
            }

            // ----------------------------
            // CTA
            // ----------------------------

            if (!nativeAd.callToAction.isNullOrBlank()) {

                cta.text = nativeAd.callToAction

                cta.visibility = View.VISIBLE

            } else {

                cta.visibility = View.GONE
            }

            nativeAdView.setNativeAd(nativeAd)
        }
    )
}