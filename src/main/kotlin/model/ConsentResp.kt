package model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConsentResp(
    @SerialName("status")
    val status: Boolean,

    @SerialName("redirect_to")
    val redirectTo: String?,

    @SerialName("message")
    val message: String
)