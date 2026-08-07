package ai.achaialabs.helios.heliosApp.app.update

import ai.achaialabs.helios.heliosApp.ActivityProvider
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AppUpdateManagerImpl : AppUpdateManager {

    private var appUpdateManager: com.google.android.play.core.appupdate.AppUpdateManager? = null

    private val _updateState =
        MutableStateFlow<UpdateResult>(UpdateResult.NoUpdate)

    override val updateState: StateFlow<UpdateResult> =
        _updateState


    private lateinit var installStateListener: InstallStateUpdatedListener

    init {
        installStateListener = InstallStateUpdatedListener { state ->

            when (state.installStatus()) {

                InstallStatus.PENDING -> {
                    _updateState.value = UpdateResult.UpdateStarted
                }

                InstallStatus.DOWNLOADING -> Unit

                InstallStatus.DOWNLOADED -> {
                    _updateState.value = UpdateResult.UpdateDownloaded
                }

                InstallStatus.INSTALLING -> Unit

                InstallStatus.INSTALLED -> {
                    appUpdateManager?.unregisterListener(installStateListener)
                    _updateState.value = UpdateResult.UpdateCompleted
                }

                InstallStatus.CANCELED -> {
                    appUpdateManager?.unregisterListener(installStateListener)
                    _updateState.value = UpdateResult.Cancelled
                }

                InstallStatus.FAILED -> {
                    appUpdateManager?.unregisterListener(installStateListener)
                    _updateState.value = UpdateResult.Error(
                        IllegalStateException("Update failed")
                    )
                }

                else -> Unit
            }
        }
    }

    override suspend fun checkForUpdate() {

        val activity = ActivityProvider.currentActivity
            ?: run {
                _updateState.value = UpdateResult.Error(
                    IllegalStateException("No active Activity")
                )
                return
            }

        val manager = AppUpdateManagerFactory.create(activity)
        appUpdateManager = manager

        suspendCancellableCoroutine<Unit> { continuation ->

            manager.appUpdateInfo
                .addOnSuccessListener { info ->

                    if (
                        info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                        info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                    ) {

                        manager.registerListener(installStateListener)

                        manager.startUpdateFlowForResult(
                            info,
                            AppUpdateType.FLEXIBLE,
                            activity,
                            UPDATE_REQUEST_CODE
                        )

                        _updateState.value = UpdateResult.UpdateStarted

                    } else {
                        _updateState.value = UpdateResult.NoUpdate
                    }

                    if (continuation.isActive) {
                        continuation.resume(Unit)
                    }
                }
                .addOnFailureListener {
                    _updateState.value = UpdateResult.Error(it)

                    if (continuation.isActive) {
                        continuation.resume(Unit)
                    }
                }
        }
    }


    override fun completeUpdate() {
        appUpdateManager?.completeUpdate()
        appUpdateManager?.unregisterListener(installStateListener)
    }

    private companion object {
        const val UPDATE_REQUEST_CODE = 1001
    }
}