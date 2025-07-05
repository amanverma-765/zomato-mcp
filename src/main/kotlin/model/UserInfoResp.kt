package model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoResp(
    @SerialName("mobile_country_isd")
    val mobileCountryIsd: Int,
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("email")
    val email: String,
    @SerialName("mobile")
    val mobile: String,
    @SerialName("theme")
    val theme: String?
)