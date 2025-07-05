package model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddressResp(
    @SerialName("status")
    val status: String,
    @SerialName("response")
    val response: Response
) {
    @Serializable
    data class Response(
        @SerialName("success_action")
        val successAction: SuccessAction?
    ) {

        @Serializable
        data class SuccessAction(
            @SerialName("type")
            val type: String,
            @SerialName("dismiss_address_page")
            val dismissAddressPage: DismissAddressPage?
        ) {
            @Serializable
            data class DismissAddressPage(
                @SerialName("address")
                val address: Address
            ) {
                @Serializable
                data class Address(
                    @SerialName("id")
                    val id: Int,
                    @SerialName("template_id")
                    val templateId: Int,
                    @SerialName("address")
                    val address: String,
                    @SerialName("address_map")
                    val addressMap: String?,
                    @SerialName("latitude")
                    val latitude: Double,
                    @SerialName("longitude")
                    val longitude: Double,
                    @SerialName("place")
                    val place: Place,
                    @SerialName("display_subtitle")
                    val displaySubtitle: String,
                    @SerialName("address_latitude")
                    val addressLatitude: Double,
                    @SerialName("address_longitude")
                    val addressLongitude: Double,
                    @SerialName("display_title")
                    val displayTitle: String,
                    @SerialName("contact_name")
                    val contactName: String,
                    @SerialName("distance_text")
                    val distanceText: String,
                    @SerialName("contact_isd_code")
                    val contactIsdCode: Int,
                    @SerialName("is_order_location")
                    val isOrderLocation: Int
                ) {
                    @Serializable
                    data class Place(
                        @SerialName("o2_serviceablity")
                        val o2Serviceablity: Boolean,
                        @SerialName("place_id")
                        val placeId: String,
                        @SerialName("place_type")
                        val placeType: String,
                        @SerialName("place_name")
                        val placeName: String,
                        @SerialName("latitude")
                        val latitude: Double,
                        @SerialName("longitude")
                        val longitude: Double
                    )
                }
            }
        }
    }
}