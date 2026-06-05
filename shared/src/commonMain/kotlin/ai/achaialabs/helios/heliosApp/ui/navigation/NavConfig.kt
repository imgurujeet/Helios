package ai.achaialabs.helios.heliosApp.ui.navigation

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
            subclass(PromptDetail::class, PromptDetail.serializer())
            subclass(ViewAll::class, ViewAll.serializer())

        }
    }
}