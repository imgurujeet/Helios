package ai.achaialabs.helios.heliosApp.domain.usecase.update

import ai.achaialabs.helios.heliosApp.app.update.AppUpdateManager
import ai.achaialabs.helios.heliosApp.app.update.UpdateResult

class CheckForUpdateUseCase(
    private val updateManager: AppUpdateManager
) {

    val updateState = updateManager.updateState

    suspend operator fun invoke() {
        updateManager.checkForUpdate()
    }

    fun completeUpdate() {
        updateManager.completeUpdate()
    }
}