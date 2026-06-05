package ai.achaialabs.helios.heliosApp.ui.profile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
    Dialog(
        onDismissRequest = onDismiss,
        properties = properties
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ),
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp) // Prevents it from hitting the very top/bottom of screen
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // TITLE
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                // OPTIONAL SUBTITLE
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // DYNAMIC CONTENT (Radio buttons, text fields, etc.)
                content()

                // OPTIONAL BOTTOM BUTTONS (Cancel, Confirm, etc.)
                if (bottomActions != null) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        content = bottomActions
                    )
                }
            }
        }
    }
}


@Composable
fun LogoutDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    HeliosDialog(
        title = "Sign Out",
        subtitle = "Are you sure you want to log out of your Helios account?",
        onDismiss = onDismiss,
        bottomActions = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)) // Red for Danger
            ) {
                Text("Log Out")
            }
        }
    ) {
        // Content is empty because we only need the title, subtitle, and buttons!
    }
}


@Composable
fun RequestPromptDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val heliosGold = Color(0xFFF59E0B)
    val heliosOrange = Color(0xF0D55900)

    HeliosDialog(
        title = "Request a Prompt",
        subtitle = "Tell us what you need, and we'll build it.",
        onDismiss = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false), // Allows wider dialog
        modifier = Modifier.fillMaxWidth(0.9f), // 90% of screen width
        bottomActions = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    onSubmit(name, message)
                    onDismiss()
                },
                enabled = name.isNotBlank() && message.isNotBlank(), // Disable if empty!
                colors = ButtonDefaults.buttonColors(containerColor = heliosOrange)
            ) {
                Text("Send Request")
            }
        }
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Your Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("What are you looking for?") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            shape = RoundedCornerShape(12.dp)
        )
    }
}


@Composable
fun ThemeSelectionDialog(
    currentIsDark: Boolean,
    onDismiss: () -> Unit,
    onThemeSelected: (Boolean) -> Unit
) {
    val heliosOrange = Color(0xF0D55900)

    HeliosDialog(
        title = "Choose Theme",
        onDismiss = onDismiss,
    ) {
        // Dark Mode Option
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                    onThemeSelected(true)
                    onDismiss()
                }
                .padding(vertical = 12.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = currentIsDark,
                onClick = null,
                colors = RadioButtonDefaults.colors(selectedColor = heliosOrange)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text("Dark Mode (Default)", color = MaterialTheme.colorScheme.onBackground)
        }

        // Light Mode Option
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                    onThemeSelected(false)
                    onDismiss()
                }
                .padding(vertical = 12.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = !currentIsDark,
                onClick = null,
                colors = RadioButtonDefaults.colors(selectedColor = heliosOrange)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text("Light Mode", color = MaterialTheme.colorScheme.onBackground)
        }
    }
}