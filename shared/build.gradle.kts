import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.aboutlibraries)
    alias(libs.plugins.buildkonfig)

}


buildkonfig {
    packageName = "ai.achaialabs.helios" // The package for the generated object

    // Logic to read from your local.properties (which is not committed to Git)
    val localProperties = Properties()
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) localProperties.load(localFile.inputStream())


    defaultConfigs {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "SUPABASE_URL",
            localProperties.getProperty("SUPABASE_URL") ?: ""
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "SUPABASE_KEY",
            localProperties.getProperty("SUPABASE_KEY") ?: ""
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "REVENUECAT_API_KEY",
            localProperties.getProperty("REVENUECAT_API_KEY") ?: ""
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "GOOGLE_CLIENT_ID",
            localProperties.getProperty("GOOGLE_CLIENT_ID") ?: ""
        )

        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "ADMOB_APP_ID", localProperties.getProperty("ADMOB_APP_ID") ?: "")
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "REWARDED_AD_UNIT_ID", localProperties.getProperty("REWARDED_AD_UNIT_ID") ?: "")
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "NATIVE_AD_UNIT_ID", localProperties.getProperty("NATIVE_AD_UNIT_ID") ?: "")

    }

}






kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    android {
       namespace = "ai.achaialabs.helios.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }

    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.ktor.client.android)
            implementation(libs.androidx.media3.exoplayer)
            implementation(libs.androidx.media3.ui)
            implementation(libs.androidx.media3.common)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.okhttp)
            implementation(libs.coil.video)
            implementation(libs.koin.android)
            implementation(libs.play.services.ads)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.jetbrains.navigation3.ui)
            implementation(libs.jetbrains.material3.adaptiveNavigation3)
            implementation(libs.jetbrains.lifecycle.viewmodelNavigation3)
            implementation(libs.material.icons.extended)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
            implementation(libs.coil.kt.coil.svg)
           // implementation(libs.coil.video)

            implementation(libs.kotlinx.datetime)

            //room
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            //koin
            api(libs.koin.compose)
            api(libs.koin.compose.viewmodel)
            //ktor
            implementation(libs.ktor.client.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.json)

            implementation(libs.compottie)
            implementation(libs.compottie.dot)
            implementation(libs.compottie.resources)

            implementation(libs.supabase.postgrest)
            implementation(libs.supabase.auth)
            implementation(libs.supabase.compose.auth)
            implementation(libs.supabase.storage)
            //revenuecat
            implementation(libs.revenuecat.core)
            implementation(libs.revenuecat.ui)
            implementation(libs.androidx.datastore.preferences.core)
            implementation(libs.okio)

            implementation(libs.aboutlibraries.compose.m3)
            implementation(libs.paging.common)
            implementation(libs.paging.compose.common)
            implementation(libs.androidx.room.paging)
            implementation(libs.androidx.paging.common)

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(
                "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2"
            )
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)

}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    // add("kspIosX64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)



}




aboutLibraries {
    export {
        // This is the specific path for Compose Multiplatform resources
        outputFile = file("src/commonMain/composeResources/files/aboutlibraries.json")
        prettyPrint = true
    }
}