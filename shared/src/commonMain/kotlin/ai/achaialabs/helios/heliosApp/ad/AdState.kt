package ai.achaialabs.helios.heliosApp.ad

sealed interface RewardedAdState {
    data object Idle : RewardedAdState
    data object Loading : RewardedAdState
    data object Loaded : RewardedAdState
    data object Showing : RewardedAdState
    data class Error(val message: String) : RewardedAdState
}

sealed interface NativeAdState {

    data object Idle : NativeAdState

    data object Loading : NativeAdState

    data object Loaded : NativeAdState

    data class Error(
        val message: String
    ) : NativeAdState
}