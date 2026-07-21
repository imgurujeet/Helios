############################################
# Essential Attributes
############################################

-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes Exceptions
-keepattributes SourceFile,LineNumberTable

############################################
# Kotlin
############################################

-keep class kotlin.Metadata { *; }

############################################
# Coroutines
############################################

-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

############################################
# Kotlin Serialization
############################################

-keep class kotlinx.serialization.** { *; }
-keep class kotlinx.serialization.internal.** { *; }

-keep @kotlinx.serialization.Serializable class * { *; }

# Keep generated serializers
-keepclassmembers class **$$serializer {
    *;
}

-keepclassmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# Enum serialization safety
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-dontwarn kotlinx.serialization.**

############################################
# Supabase
############################################

-keep class io.github.jan.supabase.** { *; }
-keep class io.github.jan.supabase.auth.** { *; }

-dontwarn io.github.jan.supabase.**

############################################
# Ktor
############################################

-keep class io.ktor.** { *; }

-dontwarn io.ktor.**

############################################
# Google Sign-In
############################################

-keep class com.google.android.gms.auth.api.signin.** { *; }
-keep class com.google.android.gms.common.api.** { *; }
-keep class com.google.android.gms.tasks.** { *; }

-dontwarn com.google.android.gms.**

############################################
# Credential Manager
############################################

-keep class androidx.credentials.** { *; }
-keep class androidx.credentials.playservices.** { *; }

-dontwarn androidx.credentials.**

############################################
# Google Identity Services
############################################

-keep class com.google.android.libraries.identity.googleid.** { *; }

############################################
# Credential Manager + Google Identity FIX
############################################

-keep class androidx.credentials.provider.** { *; }
-keep class androidx.credentials.exceptions.** { *; }

-keep class com.google.android.gms.internal.** { *; }

-keep class com.google.android.libraries.identity.googleid.internal.** { *; }


-dontwarn androidx.credentials.provider.**
-dontwarn com.google.android.libraries.identity.googleid.**


############################################
# RevenueCat
############################################

-keep class com.revenuecat.** { *; }

############################################
# AdMob
############################################

-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.android.gms.ads.nativead.** { *; }

############################################
# OkHttp
############################################

-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

-dontwarn okhttp3.**

############################################
# Koin / Coil / Media3
############################################

-dontwarn org.koin.**
-dontwarn coil3.**
-dontwarn androidx.media3.**

############################################
# Java Management warnings
############################################

-dontwarn java.lang.management.**