package ai.achaialabs.promptr.promptrApp.ui.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

val navConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey :: class){
            subclass(Home::class , Home.serializer())
            subclass(Explore::class , Explore.serializer())
            subclass(Profile::class, Profile.serializer())
        }
    }
}