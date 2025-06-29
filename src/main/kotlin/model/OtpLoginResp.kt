package model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OtpLoginResp(
    @SerialName("status")
    val status: Boolean,

    @SerialName("redirect_to")
    val redirectTo: String?,

    @SerialName("message")
    val message: String?,

    @SerialName("is_oauth_enabled")
    val isOauthEnabled: Boolean?,

    @SerialName("show_call_button")
    val showCallButton: Boolean?,

    @SerialName("show_whatsapp_button")
    val showWhatsappButton: Boolean?,

    @SerialName("are_whatsapp_attempts_left")
    val areWhatsappAttemptsLeft: Boolean?,

    @SerialName("are_message_attempts_left")
    val areMessageAttemptsLeft: Boolean?,

    @SerialName("are_call_attempts_left")
    val areCallAttemptsLeft: Boolean?,

    @SerialName("message_uuid")
    val messageUuid: String?,

    @SerialName("is_dietary_preference_received")
    val isDietaryPreferenceReceived: Boolean?,

    @SerialName("should_hide_skip_button")
    val shouldHideSkipButton: Boolean?
)
