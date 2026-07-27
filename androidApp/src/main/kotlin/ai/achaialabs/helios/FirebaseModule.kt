package ai.achaialabs.helios

import ai.achaialabs.helios.firebase.inappmessaging.InAppMessagingServiceImpl
import ai.achaialabs.helios.firebase.analytics.AnalyticsServiceImpl
import ai.achaialabs.helios.firebase.crashlytics.CrashlyticsServiceImpl
import ai.achaialabs.helios.firebase.fcm.PushNotificationServiceImpl
import ai.achaialabs.helios.heliosApp.firebase.Inappmessaging.InAppMessagingService

import ai.achaialabs.helios.heliosApp.firebase.analytics.AnalyticsService
import ai.achaialabs.helios.heliosApp.firebase.crashlytics.CrashlyticsService
import ai.achaialabs.helios.heliosApp.firebase.fcm.PushNotificationService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val firebaseModule = module {

    single<AnalyticsService> {
        AnalyticsServiceImpl()
    }

    single<CrashlyticsService> {
        CrashlyticsServiceImpl()
    }

    single<PushNotificationService> {
        PushNotificationServiceImpl(
            androidContext()
        )
    }

    single<InAppMessagingService> {
        InAppMessagingServiceImpl()
    }
}