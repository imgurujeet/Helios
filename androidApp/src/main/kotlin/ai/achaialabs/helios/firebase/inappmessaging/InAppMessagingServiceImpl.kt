package ai.achaialabs.helios.firebase.inappmessaging

import ai.achaialabs.helios.heliosApp.firebase.Inappmessaging.InAppMessagingService
import com.google.firebase.inappmessaging.FirebaseInAppMessaging

class InAppMessagingServiceImpl : InAppMessagingService {

    private val inAppMessaging = FirebaseInAppMessaging.getInstance()

    override fun triggerEvent(event: String) {
        inAppMessaging.triggerEvent(event)
    }

    override fun setMessagesSuppressed(suppressed: Boolean) {
        inAppMessaging.setMessagesSuppressed(suppressed)
    }
}