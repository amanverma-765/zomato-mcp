package model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationReq(
    @SerialName("horizontal_accuracy")
    val horizontalAccuracy: Double,
    @SerialName("is_precise_location_denied")
    val isPreciseLocationDenied: Boolean,
    @SerialName("lon")
    val lon: Double,
    @SerialName("source")
    val source: String,
    @SerialName("res_id")
    val resId: Int,
    @SerialName("lat")
    val lat: Double,
    @SerialName("timestamp")
    val timestamp: Long,
    @SerialName("is_gps_permission_denied")
    val isGpsPermissionDenied: Boolean,
    @SerialName("device_lat")
    val deviceLat: Double,
    @SerialName("is_manual_auto_detect")
    val isManualAutoDetect: Boolean,
    @SerialName("force_auto_detect")
    val forceAutoDetect: Boolean,
    @SerialName("address_id")
    val addressId: Int,
    @SerialName("is_nu_address_screen")
    val isNuAddressScreen: Boolean,
    @SerialName("is_address_flow")
    val isAddressFlow: Boolean,
    @SerialName("device_lon")
    val deviceLon: Double,
    @SerialName("is_device_location_permission_denied")
    val isDeviceLocationPermissionDenied: Boolean,
    @SerialName("should_check_for_address")
    val shouldCheckForAddress: Boolean
)