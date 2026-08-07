package ai.achaialabs.helios.heliosApp.di

import ai.achaialabs.helios.heliosApp.ad.AdManager
import ai.achaialabs.helios.heliosApp.ad.AndroidAdManager
import ai.achaialabs.helios.heliosApp.app.review.AppReviewManager
import ai.achaialabs.helios.heliosApp.app.review.AppReviewManagerImpl
import ai.achaialabs.helios.heliosApp.app.update.AppUpdateManager
import ai.achaialabs.helios.heliosApp.app.update.AppUpdateManagerImpl
import ai.achaialabs.helios.heliosApp.data.datastore.DataStoreProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    // Passes the Android context into the actual class
    single { DataStoreProvider(androidContext()) }
    single<AdManager> { AndroidAdManager(androidContext()) }

    single<AppReviewManager> {
        AppReviewManagerImpl()
    }

    single<AppUpdateManager> {
        AppUpdateManagerImpl()
    }

}