package ai.achaialabs.promptr.promptrApp.di

import ai.achaialabs.promptr.promptrApp.core.network.createSupabaseClient
import ai.achaialabs.promptr.promptrApp.data.local.dao.HomeHeroDao
import ai.achaialabs.promptr.promptrApp.data.local.dao.PromptDao
import ai.achaialabs.promptr.promptrApp.data.local.dao.UserDao
import ai.achaialabs.promptr.promptrApp.data.local.database.PromptDatabase
import ai.achaialabs.promptr.promptrApp.data.local.database.getDatabase
import ai.achaialabs.promptr.promptrApp.data.local.database.getDatabaseBuilder
import ai.achaialabs.promptr.promptrApp.data.local.datasource.*
import ai.achaialabs.promptr.promptrApp.data.remote.datasource.AuthRemoteDataSource
import ai.achaialabs.promptr.promptrApp.data.remote.datasource.AuthRemoteDataSourceImpl
import ai.achaialabs.promptr.promptrApp.data.remote.datasource.PromptRemoteDataSource
import ai.achaialabs.promptr.promptrApp.data.remote.datasource.PromptRemoteDataSourceImpl
import ai.achaialabs.promptr.promptrApp.data.repository.AuthRepositoryImpl
import ai.achaialabs.promptr.promptrApp.data.repository.PromptRepositoryImpl
import ai.achaialabs.promptr.promptrApp.domain.repository.AuthRepository
import ai.achaialabs.promptr.promptrApp.domain.repository.PromptRepository
import ai.achaialabs.promptr.promptrApp.domain.usecase.*
import ai.achaialabs.promptr.promptrApp.domain.usecase.auth.*
import ai.achaialabs.promptr.promptrApp.ui.home.HomeViewModel
import ai.achaialabs.promptr.promptrApp.ui.onboarding.LoginViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Supabase
    single { createSupabaseClient() }

    // Database
    single { getDatabaseBuilder() }
    single { getDatabase(get()) }
    single { get<PromptDatabase>().promptDao() }
    single { get<PromptDatabase>().homeHeroDao() }
    single { get<PromptDatabase>().userDao() }

    // Local Data Sources
    single<PromptLocalDataSource> { PromptLocalDataSourceImpl(get()) }
    single<HomeHeroLocalDataSource> { HomeHeroLocalDataSourceImpl(get()) }
    single<AuthLocalDataSource> { AuthLocalDataSourceImpl(userDao = get()) }

    // Remote Data Sources
    single<AuthRemoteDataSource> { AuthRemoteDataSourceImpl(get()) }
    single<PromptRemoteDataSource> { PromptRemoteDataSourceImpl(get()) }

    // Repositories
    single<PromptRepository> {
        PromptRepositoryImpl(
            get(),
            get(),
            get()
        )
    }
    single<AuthRepository> {
        AuthRepositoryImpl(get(), get(), get())
    }

    // Use Cases
    factory { RefreshHomeDataUseCase(get()) }
    factory { GetHomePromptsUseCase(get()) }
    factory { GetHomeHeroesUseCase(get()) }
    factory { ToggleLikeUseCase(get()) }
    factory { ToggleBookmarkUseCase(get()) }
    factory { IsLoggedInUseCase(get()) }
    factory { LoginUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { SyncUserUseCase(get()) }
    factory {
        GetCurrentUserUseCase(get())
    }

    // ViewModels
    viewModel {
        HomeViewModel(get(), get(), get(), get(),get())
    }
    viewModel {
        LoginViewModel(get(), get())
    }
}
