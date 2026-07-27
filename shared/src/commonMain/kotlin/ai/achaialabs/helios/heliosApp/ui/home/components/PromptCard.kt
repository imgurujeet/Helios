package ai.achaialabs.helios.heliosApp.ui.home.components

import ai.achaialabs.helios.heliosApp.ui.media.MediaRenderer
import ai.achaialabs.helios.heliosApp.ui.model.PromptUi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import helios.shared.generated.resources.Res
import helios.shared.generated.resources.ic_share
import helios.shared.generated.resources.ic_star
import helios.shared.generated.resources.ic_star_filled

@Composable
fun PromptCard(
    prompt: PromptUi,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    onCardClick: () -> Unit = {},
    onPlayClick: () -> Unit = {},
    onLikeClick: () -> Unit = {},
    onShareClick: () -> Unit = {}
) {

    Card(
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                }
            ) {
                onCardClick()
            },
        shape = RoundedCornerShape(24.dp)
    ) {

        Box {

            MediaRenderer(
                media = prompt.media,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                isPlaying = isPlaying,
                onPlayClick = onPlayClick
            )
            // BOTTOM CONTENT

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
            ) {

//                Text(
//                    text = prompt.title,
//                    maxLines = 2,
//                    overflow = TextOverflow.Ellipsis,
//                    style = androidx.compose.material3.MaterialTheme.typography.labelLarge,
//                    fontWeight = FontWeight.Bold,
//                    color = Color.White
//                )

                Spacer(Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    // LIKE

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember {
                                    MutableInteractionSource()
                                }
                            ) {
                                onLikeClick()
                            },
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = if(prompt.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                           // painter = if(prompt.isLiked) painterResource(Res.drawable.ic_star_filled) else painterResource(Res.drawable.ic_star),
                            contentDescription = null,
                            tint = if(prompt.isLiked) {
                                Color(0xF0D55900)
                            } else {
                                Color.White
                            },
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = prompt.likesCount.toString(),
                        color = Color.White,
                        fontSize = 12.sp,
                       // modifier = modifier.align(Alignment.Bottom)
                    )

                    Spacer(Modifier.weight(1f))

                    // SHARE

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember {
                                    MutableInteractionSource()
                                }
                            ) {
                                onShareClick()
                            },
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.Share,
                            //painter = painterResource(Res.drawable.ic_share),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}