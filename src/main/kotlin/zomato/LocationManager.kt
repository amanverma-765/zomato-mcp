package com.ark.zomato

import co.touchlab.kermit.Logger
import com.ark.utils.AppConstants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.http.isSuccess
import model.LocationReq
import model.LocationResp
import model.LocationTokenResp

internal class LocationManager(private val httpClient: HttpClient) {

    val authHeaders = mapOf(
        "Accept-Encoding" to "br, gzip, deflate",
        "Host" to AppConstants.API_HOST,
    )

    suspend fun getLocationDetails(
        latitude: Double,
        longitude: Double,
        horizontalAccuracy: Double,
    ): LocationResp {
        val locationUrl = URLBuilder("https://${AppConstants.API_HOST}/gw/tabbed-location").apply {
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
            source = "home",
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
            shouldCheckForAddress = true
        )

        val response = httpClient.post(locationUrl) {
            headers {
                authHeaders.forEach { (key, value) -> append(key, value) }
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

    suspend fun registerLocation(locationData: LocationResp): LocationTokenResp {
        val loc = locationData.location
        val place = loc.place

        val locRegUrl = URLBuilder("https://api.zomato.com/gw/tabbed-home").apply {
            parameters.apply {
                append("cell_id", place.cellId)
                append("entity_name", loc.entityName)
                append("device_lat", place.latitude)
                append("device_lon", place.longitude)
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
                append("did_show_tour", "0")
                append("should_not_detect_nearby_address", "0")
                append("horizontal_accuracy", "1")
                append("is_gold_mode_on", "0")
                append("is_gps_permission_denied", "false")
                append("is_precise_location_denied", "false")
                append("force_entity_title", loc.entityTitle)
                append("force_entity_subtitle", loc.entitySubtitle)
                append("android_country", "IN")
                append("lang", "en")
                append("android_language", "en")
                append("source", "App_Launch")
                append("show_lang_bottom_sheet", "false")
                append("showing_diet_preferences", "false")
                append("is_device_location_permission_denied", "false")
                append("current_app_icon_type", "default")
                append("gold_icon_prompt_denied", "false")
            }
        }.buildString()

        val response = httpClient.get(locRegUrl) {
            headers {
                authHeaders.forEach { (key, value) -> append(key, value) }
            }
        }

        if (!response.status.isSuccess()) {
            Logger.e("Failed to register location: ${response.status.description}")
            throw IllegalStateException(response.status.description)
        }
        return response.body<LocationTokenResp>()
    }

}