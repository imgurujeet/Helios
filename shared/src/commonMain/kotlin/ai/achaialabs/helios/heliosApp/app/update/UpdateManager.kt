package ai.achaialabs.helios.heliosApp.app.update

import kotlinx.coroutines.flow.StateFlow

sealed interface UpdateResult {

    data object NoUpdate : UpdateResult

    data object UpdateStarted : UpdateResult

    data object UpdateDownloaded : UpdateResult

    data object UpdateCompleted : UpdateResult

    data object Cancelled : UpdateResult

    data class Error(
        val throwable: Throwable
    ) : UpdateResult
}

interface AppUpdateManager {

    val updateState: StateFlow<UpdateResult>

    suspend fun checkForUpdate()

    fun completeUpdate()
}