package model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationRegReq(
    @SerialName("selected_location_data")
    val selectedLocationData: SelectedLocationData,
    @SerialName("user_defined_location_data")
    val userDefinedLocationData: UserDefinedLocationData,
    @SerialName("source")
    val source: String,
    @SerialName("custom_data")
    val customData: CustomData,
    @SerialName("postback_params")
    val postbackParams: String,
    @SerialName("device_location_data")
    val deviceLocationData: DeviceLocationData
) {
    @Serializable
    data class SelectedLocationData(
        @SerialName("latitude")
        val latitude: Double,
        @SerialName("longitude")
        val longitude: Double
    )

    @Serializable
    data class UserDefinedLocationData(
        @SerialName("latitude")
        val latitude: Double,
        @SerialName("longitude")
        val longitude: Double
    )

    @Serializable
    data class CustomData(
        @SerialName("id_location_info")
        val idLocationInfo: String,
        @SerialName("id_phone")
        val idPhone: String,
        @SerialName("id_location_address_input")
        val idLocationAddressInput: String,
        @SerialName("id_location_type")
        val idLocationType: String,
        @SerialName("id_selection_other")
        val idSelectionOther: String,
        @SerialName("id_name")
        val idName: String
    )

    @Serializable
    data class DeviceLocationData(
        @SerialName("latitude")
        val latitude: Double,
        @SerialName("longitude")
        val longitude: Double
    )
}


@Serializable
data class PostbackParams(
    @SerialName("isd_code")
    val isdCode: Int,

    @SerialName("dsz_id")
    val dszId: Int,

    @SerialName("subzone_id")
    val subzoneId: Int,

    @SerialName("dsz_name")
    val dszName: String,

    @SerialName("dsz_latitude")
    val dszLatitude: Double,

    @SerialName("dsz_longitude")
    val dszLongitude: Double,

    val latitude: Double,
    val longitude: Double,

    @SerialName("force_display_title")
    val forceDisplayTitle: String,

    @SerialName("force_display_subtitle")
    val forceDisplaySubtitle: String,

    @SerialName("initial_latitude")
    val initialLatitude: Double,

    @SerialName("initial_longitude")
    val initialLongitude: Double,

    @SerialName("entity_title")
    val entityTitle: String,

    @SerialName("selected_pill_id")
    val selectedPillId: String,

    @SerialName("contact_name")
    val contactName: String,

    @SerialName("contact_phone")
    val contactPhone: String,

    @SerialName("is_res_serviceable")
    val isResServiceable: Boolean,

    @SerialName("country_id")
    val countryId: Int
)

