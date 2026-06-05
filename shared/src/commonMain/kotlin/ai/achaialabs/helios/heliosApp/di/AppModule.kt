package ai.achaialabs.helios.heliosApp.di

import ai.achaialabs.helios.heliosApp.core.network.createSupabaseClient
import ai.achaialabs.helios.heliosApp.data.local.database.PromptDatabase
import ai.achaialabs.helios.heliosApp.data.local.database.getDatabase
import ai.achaialabs.helios.heliosApp.data.local.database.getDatabaseBuilder
import ai.achaialabs.helios.heliosApp.data.local.datasource.*
import ai.achaialabs.helios.heliosApp.data.remote.datasource.AuthRemoteDataSource
import ai.achaialabs.helios.heliosApp.data.remote.datasource.AuthRemoteDataSourceImpl
import ai.achaialabs.helios.heliosApp.data.remote.datasource.ExploreRemoteDataSource
import ai.achaialabs.helios.heliosApp.data.remote.datasource.PromptRemoteDataSource
import ai.achaialabs.helios.heliosApp.data.remote.datasource.PromptRemoteDataSourceImpl
import ai.achaialabs.helios.heliosApp.data.remote.datasource.ToolRemoteDataSource
import ai.achaialabs.helios.heliosApp.data.repository.AuthRepositoryImpl
import ai.achaialabs.helios.heliosApp.data.repository.ExploreRepositoryImpl
import ai.achaialabs.helios.heliosApp.data.repository.PromptDetailRepositoryImpl
import ai.achaialabs.helios.heliosApp.data.repository.PromptRepositoryImpl
import ai.achaialabs.helios.heliosApp.domain.repository.AuthRepository
import ai.achaialabs.helios.heliosApp.domain.repository.ExploreRepository
import ai.achaialabs.helios.heliosApp.domain.repository.PromptDetailRepository
import ai.achaialabs.helios.heliosApp.domain.repository.PromptRepository
import ai.achaialabs.helios.heliosApp.domain.usecase.*
import ai.achaialabs.helios.heliosApp.domain.usecase.auth.*
import ai.achaialabs.helios.heliosApp.domain.usecase.explore.ObserveExploreFeedUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.explore.SyncExploreFeedUseCase
import ai.achaialabs.helios.heliosApp.app.MainViewModel
import ai.achaialabs.helios.heliosApp.data.remote.service.SubscriptionManager
import ai.achaialabs.helios.heliosApp.domain.usecase.viewall.ObservePromptsByCategoryUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.viewall.SyncPromptsByCategoryUseCase
import ai.achaialabs.helios.heliosApp.ui.explore.ExploreViewModel
import ai.achaialabs.helios.heliosApp.ui.home.HomeViewModel
import ai.achaialabs.helios.heliosApp.ui.onboarding.LoginViewModel
import ai.achaialabs.helios.heliosApp.ui.profile.ProfileViewModel
import ai.achaialabs.helios.heliosApp.ui.promptDetail.PromptDetailViewModel
import ai.achaialabs.helios.heliosApp.ui.viewall.ViewAllViewModel
import androidx.navigation3.runtime.NavKey
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel
import kotlin.coroutines.EmptyCoroutineContext.get

val appModule = module {
    // Supabase
    single { createSupabaseClient() }

    // Database
    single { getDatabaseBuilder() }
    single { getDatabase(get()) }
    single { get<PromptDatabase>().promptDao() }
    single { get<PromptDatabase>().homeHeroDao() }
    single { get<PromptDatabase>().userDao() }
    single { get<PromptDatabase>().exploreDao() }
    single { get<PromptDatabase>().toolDao() }

    single { SubscriptionManager(get()) }

    // Local Data Sources
    single<PromptLocalDataSource> { PromptLocalDataSourceImpl(get()) }
    single<HomeHeroLocalDataSource> { HomeHeroLocalDataSourceImpl(get()) }
    single<AuthLocalDataSource> { AuthLocalDataSourceImpl(userDao = get()) }



    // Remote Data Sources
    single<AuthRemoteDataSource> { AuthRemoteDataSourceImpl(get()) }
    single<PromptRemoteDataSource> { PromptRemoteDataSourceImpl(get()) }
    single<ExploreRemoteDataSource> { ExploreRemoteDataSource(get()) }
    single<ToolRemoteDataSource> { ToolRemoteDataSource(get()) }


    // Repositories
    single<PromptRepository> {
        PromptRepositoryImpl(
            get(),
            get(),
            get(),
        )
    }
    single<AuthRepository> {
        AuthRepositoryImpl(get(), get(), get())
    }
    single<ExploreRepository> {
        ExploreRepositoryImpl(get(), get())
    }
    single<PromptDetailRepository>{
        PromptDetailRepositoryImpl(get(), get())
    }

    // Use Cases
    factory { RefreshHomeDataUseCase(get()) }
    factory { GetHomePromptsUseCase(get()) }
    factory { GetHomeHeroesUseCase(get()) }
    factory { SyncHomePromptsUseCase(get()) }
    factory { ToggleLikeUseCase(get()) }
    factory { ToggleBookmarkUseCase(get()) }
    factory { IsLoggedInUseCase(get()) }
    factory { LoginUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { SyncUserUseCase(get()) }
    factory {
        GetCurrentUserUseCase(get())
    }
    factory {
        ObserveExploreFeedUseCase(get())
    }
    factory {
        SyncExploreFeedUseCase(get())
    }
    factory {
        ObservePromptsByCategoryUseCase(get())
    }
    factory {
        SyncPromptsByCategoryUseCase(get())
    }
    factory {
        ObserveToolsUseCase(get())
    }
    factory {
        SyncToolsUseCase(get())
    }

    // ViewModels
    viewModel {
        HomeViewModel(get(), get(), get(), get(),get(),get(),get())
    }
    viewModel {
        LoginViewModel(get(), get())
    }

    viewModel {
        ExploreViewModel(get(), get())
    }
    viewModel { (id: String, name: String) ->
        ViewAllViewModel(
            categoryId = id,
            categoryName = name,
            observePrompts = get(),
            syncPrompts = get(),
            get()
        )
    }

    viewModel { (categoryId: String?) ->
        PromptDetailViewModel(
            categoryId = categoryId, // Passed straight from the UI
            observePromptsByCategoryUseCase = get(), // The new UseCase we added!
            getHomePromptsUseCase = get(),
            observeToolsUseCase = get(),
            syncToolsUseCase = get(),
            toggleLikeUseCase = get(),
            toggleBookmarkUseCase = get(),
            getCurrentUserUseCase = get()
        )
    }

    viewModel{
        ProfileViewModel(get(), get(), get(), get())
    }

    viewModel {
        MainViewModel(get(), get(), get(), get())
    }
}
