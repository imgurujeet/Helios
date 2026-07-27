package ai.achaialabs.helios.heliosApp.firebase.Inappmessaging

interface InAppMessagingService {

    fun triggerEvent(event: String)

    fun setMessagesSuppressed(
        suppressed: Boolean
    )
}