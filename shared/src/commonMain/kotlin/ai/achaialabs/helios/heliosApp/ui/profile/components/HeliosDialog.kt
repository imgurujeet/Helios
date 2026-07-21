package ai.achaialabs.helios.heliosApp.ui.profile.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties


@Composable
fun HeliosDialog(
    title: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    properties: DialogProperties = DialogProperties(),
    bottomActions: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(onDismissRequest = onDismiss, properties = properties) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            modifier = modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(text = title, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(24.dp))
                content()
                if (bottomActions != null) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, content = bottomActions)
                }
            }
        }
    }
}

@Composable
fun HeliosOptionItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    activeColor: Color = Color(0xFFD55900), // Adjusted to valid Hex
    icon: ImageVector? = null,
    showRadioButton: Boolean = true // NEW: Toggle this off for action links
) {
    val backgroundColor by animateColorAsState(if (isSelected) activeColor.copy(alpha = 0.1f) else Color.Transparent)
    val borderColor by animateColorAsState(if (isSelected) activeColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(icon, null, tint = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
        }
        Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal, modifier = Modifier.weight(1f))

        if (showRadioButton) {
            RadioButton(selected = isSelected, onClick = null, colors = RadioButtonDefaults.colors(selectedColor = activeColor))
        }
    }
}

// Dialogs remain unchanged in logic, but now automatically use the updated OptionItem!
@Composable
fun ThemeSelectionDialog(currentIsDark: Boolean, onDismiss: () -> Unit, onThemeSelected: (Boolean) -> Unit) {
    HeliosDialog(title = "Choose Theme", onDismiss = onDismiss) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            HeliosOptionItem("Dark Mode", currentIsDark, { onThemeSelected(true); onDismiss() }, icon = Icons.Rounded.DarkMode)
            HeliosOptionItem("Light Mode", !currentIsDark, { onThemeSelected(false); onDismiss() }, icon = Icons.Rounded.LightMode)
        }
    }
}

@Composable
fun LogoutDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    HeliosDialog(title = "Sign Out", subtitle = "Are you sure you want to log out?", onDismiss = onDismiss, bottomActions = {
        TextButton(onClick = onDismiss) { Text("Cancel",color = MaterialTheme.colorScheme.onSurface) }
        Button(onClick = onConfirm, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(
            0xFFEF4444
        )
        )) { Text("Log Out", color = MaterialTheme.colorScheme.onSurface) }
    }) {}
}


@Composable
fun RequestPromptDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    // Max length for the prompt to keep feedback focused
    val maxMessageLength = 500

    HeliosDialog(
        title = "Request a Prompt",
        subtitle = "Tell us what you need, and we'll build it.",
        onDismiss = onDismiss,
        bottomActions = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurface)
            }
            Button(
                onClick = { onSubmit(name, message); onDismiss() },
                // Disable if name is empty OR message is empty
                enabled = name.isNotBlank() && message.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD55900))
            ) {
                Text("Send Request", fontWeight = FontWeight.Bold,color = MaterialTheme.colorScheme.onSurface)
            }
        }
    ) {
        // Name Field
        HeliosTextField(
            value = name,
            onValueChange = { name = it },
            label = "Your Name"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Message Field with Character Counter
        Column {
            HeliosTextField(
                value = message,
                onValueChange = { if (it.length <= maxMessageLength) message = it },
                label = "What are you looking for?",
                height = 120.dp
            )
            Text(
                text = "${message.length} / $maxMessageLength",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun FeedbackDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    var category by remember { mutableStateOf("Bug") } // Shortened for better fit
    var comment by remember { mutableStateOf("") }

    HeliosDialog(
        title = "Send Feedback",
        subtitle = "Help us improve Helios.",
        onDismiss = onDismiss,
        bottomActions = {
            TextButton(onClick = onDismiss) { Text("Cancel",color = MaterialTheme.colorScheme.onSurface) }
            Button(
                onClick = { onSubmit(category, comment); onDismiss() },
                enabled = comment.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD55900))
            ) { Text("Submit",color = MaterialTheme.colorScheme.onSurface) }
        }
    ) {
        Text("Category", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))

        // SIDE-BY-SIDE LAYOUT
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.weight(1f)) {
                HeliosOptionItem("Bug", category == "Bug", { category = "Bug" }, showRadioButton = true)
            }
            Box(Modifier.weight(1f)) {
                HeliosOptionItem("Idea", category == "Idea", { category = "Idea" }, showRadioButton = true)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HeliosTextField(value = comment, onValueChange = { comment = it }, label = "Your Feedback", height = 100.dp, singleLine = false)
    }
}

// Helper to keep TextFields squared and consistent
@Composable
fun HeliosTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    height: androidx.compose.ui.unit.Dp = 56.dp,
    singleLine: Boolean = true // Added this
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().height(height),
        shape = RoundedCornerShape(12.dp),
        singleLine = singleLine, // Prevents vertical cropping
        maxLines = if (singleLine) 1 else 5
    )
}