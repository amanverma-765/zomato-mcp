package model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginTokenResponse(
    @SerialName("status")
    val status: Boolean,

    @SerialName("message")
    val message: String,

    @SerialName("access_token")
    val accessToken: String?,

    @SerialName("user")
    val user: User?,

    @SerialName("token")
    val token: Token?,

    @SerialName("should_show_notification_permission_screen")
    val showNotificationPerm: Boolean?
) {
    @Serializable
    data class User(
        @SerialName("id")
        val id: Int,

        @SerialName("is_new_user")
        val isNewUser: Boolean
    )

    @Serializable
    data class Token(
        @SerialName("access_token")
        val accessToken: String,

        @SerialName("refresh_token")
        val refreshToken: String,

        @SerialName("expires_at")
        val expiresAt: String,

        @SerialName("type")
        val type: String
    )
}