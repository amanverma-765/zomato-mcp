package com.ark.zomato

import co.touchlab.kermit.Logger
import com.ark.model.DeliveryType
import com.ark.utils.AppConstants
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.*
import io.ktor.http.headers
import kotlinx.serialization.json.Json
import model.*

internal class AddressManager(private val httpClient: HttpClient) {

    val locationHeaders = mapOf(
        "Accept-Encoding" to "br, gzip, deflate",
        "Host" to AppConstants.API_HOST,
    )

    suspend fun getLocationDetails(
        latitude: Double,
        longitude: Double,
        horizontalAccuracy: Int,
    ): LocationResp {
        val url = URLBuilder("https://${AppConstants.API_HOST}/gw/tabbed-location").apply {
            parameters.append("android_country", "IN")
            parameters.append("lang", "en")
            parameters.append("android_language", "en")
            parameters.append("response_type", "code")
            parameters.append("city_id", "-1")
        }.buildString()

        val locationReq = LocationReq(
            horizontalAccuracy = horizontalAccuracy,
            isPreciseLocationDenied = false,
            lon = longitude,
            source = "delivery_home",
            resId = 0,
            lat = latitude,
            timestamp = System.currentTimeMillis(),
            isGpsPermissionDenied = false,
            deviceLat = latitude,
            deviceLon = longitude,
            isManualAutoDetect = true,
            forceAutoDetect = true,
            addressId = 0,
            isNuAddressScreen = false,
            isAddressFlow = false,
            isDeviceLocationPermissionDenied = false,
            shouldCheckForAddress = false
        )

        val response = httpClient.post(url) {
            headers {
                locationHeaders.forEach { (key, value) -> append(key, value) }
            }
            contentType(ContentType.Application.Json)
            setBody(locationReq)
        }

        if (!response.status.isSuccess()) {
            Logger.e("Failed to get Location details: ${response.status.description}")
            throw IllegalStateException(response.status.description)
        }
        return response.body<LocationResp>()
    }

    suspend fun getLocationToken(locationResp: LocationResp, addressId: Int? = null): LocationTokenResp {
        val loc = locationResp.location!!
        val place = loc.place

        val url = URLBuilder("https://api.zomato.com/gw/tabbed-home").apply {
            parameters.apply {
                append("cell_id", place.cellId)
                append("entity_name", loc.entityName)
                append("device_lat", loc.entityLatitude.toString())
                append("device_lon", loc.entityLongitude.toString())
                append("user_defined_latitude", loc.userDefinedLatitude.toString())
                append("user_defined_longitude", loc.userDefinedLongitude.toString())
                append("place_name", place.placeName)
                append("current_poi_id", loc.currentPoiId.toString())
                append("entity_id", loc.entityId.toString())
                append("location_type", place.locationType)
                append("is_order_location", loc.isOrderLocation.toString())
                append("entity_type", loc.entityType)
                append("place_type", place.placeType)
                append("place_id", place.placeId)
                append("delivery_subzone_alias_id", "0")
                append("is_google_search_enabled", "0")
                append("city_id", loc.city.id.toString())
                append("force_entity_title", loc.entityTitle)
                append("force_entity_subtitle", loc.entitySubtitle)
                addressId?.let { append("address_id", it.toString()) }
                append("did_show_tour", "0")
                append("should_not_detect_nearby_address", "0")
                append("horizontal_accuracy", "1")
                append("is_gold_mode_on", "0")
                append("is_gps_permission_denied", "false")
                append("is_precise_location_denied", "false")
                append("android_country", "IN")
                append("lang", "en")
                append("android_language", "en")
                append("source", "Delivery_Home")
                append("show_lang_bottom_sheet", "false")
                append("showing_diet_preferences", "false")
                append("is_device_location_permission_denied", "false")
                append("current_app_icon_type", "default")
                append("gold_icon_prompt_denied", "false")
            }
        }.buildString()

        val response = httpClient.get(url) {
            headers {
                locationHeaders.forEach { (key, value) -> append(key, value) }
            }
        }

        if (!response.status.isSuccess()) {
            Logger.e("Failed to register location: ${response.status.description}")
            throw IllegalStateException(response.status.description)
        }
        return response.body<LocationTokenResp>()
    }

    suspend fun registerAddress(
        locationTokenResp: LocationTokenResp,
        userInfo: UserInfoResp,
        deliveryType: DeliveryType,
        deliveryTypeIfOther: String,
        additionalData: String,
    ): AddressResp {
        val url = "https://${AppConstants.API_HOST}/gw/user/address/save"
        val location = locationTokenResp.location

        val postbackParams = PostbackParams(
            dszId = location.place.deliverySubzoneId,
            subzoneId = location.entityId,
            dszName = location.entityTitle.replace(",", ""),
            dszLatitude = location.userDefinedLatitude,
            dszLongitude = location.userDefinedLongitude,
            latitude = location.entityLatitude,
            longitude = location.entityLongitude,
            initialLatitude = location.entityLatitude,
            initialLongitude = location.entityLongitude,
            entityTitle = location.entityTitle,
            selectedPillId = "id_selection_home",
            contactName = userInfo.name,
            isResServiceable = true,
            countryId = location.city.countryId,
            isdCode = userInfo.mobileCountryIsd,
            forceDisplayTitle = location.displayTitle.replace(",", ""),
            forceDisplaySubtitle = location.entitySubtitle.replace(",", ""),
            contactPhone = userInfo.mobile
        )

        val registrationRequest = LocationRegReq(
            selectedLocationData = LocationRegReq.SelectedLocationData(
                latitude = location.entityLatitude,
                longitude = location.entityLongitude
            ),
            userDefinedLocationData = LocationRegReq.UserDefinedLocationData(
                latitude = location.userDefinedLatitude,
                longitude = location.userDefinedLongitude
            ),
            source = "delivery_home",
            customData = LocationRegReq.CustomData(
                idLocationInfo = location.entityName,
                idPhone = userInfo.mobile,
                idLocationAddressInput = additionalData,
                idLocationType = "id_selection_${deliveryType.value}",
                idSelectionOther = if (deliveryType == DeliveryType.OTHER) deliveryTypeIfOther else "",
                idName = userInfo.name
            ),
            postbackParams = Json.encodeToString(postbackParams),
            deviceLocationData = LocationRegReq.DeviceLocationData(
                latitude = location.entityLatitude,
                longitude = location.entityLongitude
            )
        )

        val response = httpClient.post(url) {
            headers {
                locationHeaders.forEach { (key, value) -> append(key, value) }
            }
            contentType(ContentType.Application.Json)
            setBody(registrationRequest)
        }

        if (!response.status.isSuccess()) {
            val error = "Failed to register address: ${response.status.description}"
            throw IllegalStateException(error)
        }

        return response.body<AddressResp>()
    }

    suspend fun removeAddress(addressId: String) {
        val url =  URLBuilder("https://api.zomato.com/v2/order/address/remove_user_address.json").apply {
            parameters.apply {
                append("android_country", "IN")
                append("lang", "en")
                append("android_language", "en")
                append("city_id", "-1")
            }
        }.buildString()

        val response = httpClient.submitForm(
            url = url,
            formParameters = Parameters.build {
                append("address_id", addressId)
            }
        ) {
            headers { locationHeaders.forEach { (key, value) -> append(key, value) } }
        }
        if (!response.status.isSuccess()) {
            val error = "Failed to remove address: ${response.status.description}"
            throw IllegalStateException(error)
        }
    }


}