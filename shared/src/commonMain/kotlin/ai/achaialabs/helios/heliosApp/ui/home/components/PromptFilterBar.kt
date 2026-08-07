package ai.achaialabs.helios.heliosApp.ui.home.components

import ai.achaialabs.helios.heliosApp.domain.filter.PromptFilter
import ai.achaialabs.helios.heliosApp.domain.model.HomeFeedType
import ai.achaialabs.helios.heliosApp.utils.Haptics
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ButtonGroupDefaults.connectedLeadingButtonShapes
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


enum class HomeTab {
    POPULAR,
    LATEST,
    REMIX
}
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PromptButtonGroup(
    selected: HomeTab,
    onSelected: (HomeTab) -> Unit,
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        ToggleButton(
            modifier = Modifier
                .weight(1f)
                .height(40.dp),
            checked = selected == HomeTab.POPULAR,
            onCheckedChange = {
                Haptics.vibrateClick()
                onSelected(HomeTab.POPULAR)
            },
            shapes = ButtonGroupDefaults.connectedLeadingButtonShapes().copy(
                checkedShape = RoundedCornerShape(50)
            ),
            colors = ToggleButtonDefaults.toggleButtonColors(

                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.6f),
                contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                checkedContainerColor = Color(0xFFF59E0B),
                checkedContentColor = MaterialTheme.colorScheme.background,

                )
        ) {
            Icon(
                Icons.Rounded.LocalFireDepartment,
                null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text("Popular")
        }

        ToggleButton(
            modifier = Modifier
                .weight(1f)
                .height(40.dp),
            checked = selected == HomeTab.LATEST,
            onCheckedChange = {
                Haptics.vibrateClick()
                onSelected(HomeTab.LATEST)
                              },
            shapes = ButtonGroupDefaults.connectedMiddleButtonShapes().copy(
                checkedShape = RoundedCornerShape(50)
            ),
            colors = ToggleButtonDefaults.toggleButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.6f),
                contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                checkedContainerColor = Color(0xFFF59E0B),
                checkedContentColor = MaterialTheme.colorScheme.background,

                )
        ) {
            Icon(
                Icons.Rounded.Schedule,
                null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text("Latest")
        }





        ToggleButton(
            modifier = Modifier
                .weight(1f)
                .height(40.dp),
            checked = selected == HomeTab.REMIX,
            onCheckedChange = {
                Haptics.vibrateClick()
                onSelected(HomeTab.REMIX)
             },
            shapes = ButtonGroupDefaults.connectedTrailingButtonShapes().copy(
                checkedShape = RoundedCornerShape(50)
            ),
            colors = ToggleButtonDefaults.toggleButtonColors(

                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.6f),
                contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                checkedContainerColor = Color(0xFFF59E0B),
                checkedContentColor = MaterialTheme.colorScheme.background,

                )
        ) {
            Icon(
                Icons.Rounded.AutoAwesome,
                null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text("Remix")
        }
    }
}