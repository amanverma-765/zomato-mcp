package model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OtpVerificationResp(
    @SerialName("status")
    val status: Boolean,

    @SerialName("are_attempts_left")
    val areAttemptsLeft: Boolean?,

    @SerialName("is_oauth_enabled")
    val isOauthEnabled: Boolean,

    @SerialName("message")
    val message: String,

    @SerialName("hash")
    val hash: String?,

    @SerialName("login_case")
    val loginCase: Int?,

    @SerialName("is_dietary_preference_received")
    val isDietaryPreferenceReceived: Boolean,

    @SerialName("redirect_to")
    val redirectTo: String?
)
