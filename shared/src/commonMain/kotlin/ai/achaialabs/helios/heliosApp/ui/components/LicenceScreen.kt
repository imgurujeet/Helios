package ai.achaialabs.helios.heliosApp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.rememberLibraries
import helios.shared.generated.resources.Res
import io.github.jan.supabase.auth.mfa.FactorType.Phone.value
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun LicensesScreen(onDismiss: () -> Unit) {
    // 1. Load the JSON asynchronously
    val jsonString by produceState<String?>(initialValue = null) {
        value = try {
            Res.readBytes("files/aboutlibraries.json").decodeToString()
        } catch (e: Exception) {
            null
        }
    }

    if (jsonString != null) {
        // Pass the string to the 'libraries' parameter instead of 'json'
        val libraries by rememberLibraries(libraries = jsonString!!)

        LibrariesContainer(
            libraries = libraries,
            header = {
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Icon Credits", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Full attribution list based on image_b7122c.png
                        val iconCredits = listOf(
                            "Opened Gift Box" to "Adrien Coquet",
                            "Y2k Star Orbit" to "Adrien Coquet",
                            "Astronaut" to "Adrien Coquet",
                            "Star" to "Alice Design",
                            "Telescope" to "Adrien Coquet",
                            "Love" to "Adrien Coquet",
                            "Planet Orbit" to "Adrien Coquet"
                        )

                        iconCredits.forEach { (icon, creator) ->
                            Text(
                                text = "• '$icon' by $creator, from Noun Project (CC BY 3.0)",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                }
            }
        )
    } else {
        // Optional: Add a loading indicator while the file is being read
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}