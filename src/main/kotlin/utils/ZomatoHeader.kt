package com.ark.utils

import kotlinx.coroutines.runBlocking
import java.util.*

internal class ZomatoHeader(
    private val authTokenStore: AuthTokenStore,
    private val sharedPref: SharedPref,
    private val locationTokenStore: LocationTokenStore
) {

    private val userAgent =
        "&source=android_market&version=16&device_manufacturer=Google&device_brand=google&device_model=Pixel+8&api_version=${AppConstants.APP_VERSION}&app_version=${AppConstants.UA_APP_VERSION}"

    private val deviceInfoHeaders = mapOf(
        "Accept" to "image/webp",
        "User-Agent" to userAgent,
        "X-Installer-Package-Name" to "com.android.vending",
        "X-RIDER-INSTALLED" to "false",
        "X-VPN-Active" to "0",
        "X-Device-Language" to "en-US",
        "X-Device-Width" to "1080",
        "X-Device-Height" to "2201",
        "X-Device-Pixel-Ratio" to "2.625",
        "X-Client-Id" to "zomato_android_v2",
        "X-APP-APPEARANCE" to "LIGHT",
        "Connection" to "keep-alive",
        "X-Zomato-Is-Metric" to "true",
        "X-Network-Type" to "wifi",
        "x-perf-class" to "PERFORMANCE_HIGH"
    )

    private val staticHeaders = mapOf(
        "X-Request-Id" to UUID.randomUUID().toString(),
        "X-Zomato-API-Key" to AppConstants.API_KEY,
        "X-Zomato-Client-Id" to AppConstants.CLIENT_ID,
        "X-Zomato-App-Version" to AppConstants.APP_VERSION,
        "X-Zomato-App-Version-Code" to AppConstants.APP_VERSION_CODE
    )

    private suspend fun optionalHeaders(): Map<String, String> {
        val locationData = locationTokenStore.getLocationTokenData()?.location
        val udLat = locationData?.userDefinedLatitude ?: 0.0
        val udLon = locationData?.userDefinedLongitude ?: 0.0
        val currLat = locationData?.entityLatitude ?: 0.0
        val currLon = locationData?.entityLongitude ?: 0.0
        val uuid = sharedPref.getString(AppConstants.ZOMATO_UUID_KEY)
        val authTokens = authTokenStore.getAllTokens()
        val androidId = sharedPref.getString(AppConstants.ANDROID_ID_KEY, "e1e55d924188ae92")

        return buildMap {
            put("X-Present-Lat", currLat.toString())
            put("X-Present-Long", currLon.toString())
            put("X-User-Defined-Lat", udLat.toString())
            put("X-User-Defined-Long", udLon.toString())
            put("X-Android-Id", androidId)
            if (uuid.isNotBlank()) put("X-Zomato-UUID", uuid)
            locationData?.horizontalAccuracy?.let { put("X-Present-Horizontal-Accuracy", it.toString()) }
            authTokens?.accessToken?.let { put("X-Access-Token", it) }
            authTokens?.token?.accessToken?.let { put("X-Zomato-Access-Token", it) }
            locationData?.token?.let { put("x-location-token", it) }
            locationData?.cityId?.let { put("X-O2-City-Id", it.toString()) }
            locationData?.cityId?.let { put("X-City-Id", it.toString()) }
        }
    }

    fun getAllHeaders(): Map<String, String> {
        return deviceInfoHeaders + staticHeaders + runBlocking { optionalHeaders() }
    }
}
