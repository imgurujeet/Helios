package ai.achaialabs.promptr.promptrApp.ui.home.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SectionTitle(
    title: String
) {

    Text(
        text = title,
        modifier = Modifier.padding(horizontal = 16.dp),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
}