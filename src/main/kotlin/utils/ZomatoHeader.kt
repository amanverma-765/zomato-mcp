package com.ark.utils

import kotlinx.coroutines.runBlocking
import java.util.*

internal class ZomatoHeader(
    private val authTokenStore: AuthTokenStore,
    private val sharedPref: SharedPref,
    private val locationDataStore: LocationDataStore
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
        "X-Device-Pixel-Ratio" to "3.0",
        "X-Client-Id" to "zomato_android_v2",
        "X-APP-APPEARANCE" to "LIGHT",
        "Connection" to "keep-alive",
        "X-Zomato-Is-Metric" to "true",
    )

    private val staticHeaders = mapOf(
        "X-Request-Id" to UUID.randomUUID().toString(),
        "X-Zomato-API-Key" to AppConstants.API_KEY,
        "X-Zomato-Client-Id" to AppConstants.CLIENT_ID,
        "X-Zomato-App-Version" to AppConstants.APP_VERSION,
        "X-Zomato-App-Version-Code" to AppConstants.APP_VERSION_CODE
    )

    private suspend fun optionalHeaders(): Map<String, String> {
        val locationData = locationDataStore.getLocationData()?.location
        val lat = locationData?.entityLatitude ?: 0.0
        val lon = locationData?.entityLongitude ?: 0.0
        val uuid = sharedPref.getString(AppConstants.ZOMATO_UUID_KEY)
        val authTokens = authTokenStore.getAllTokens()
        val androidId  = sharedPref.getString(AppConstants.ANDROID_ID_KEY, "e1e55d924188ae92")

        return buildMap {
            put("X-Present-Lat", lat.toString())
            put("X-Present-Long", lon.toString())
            put("X-User-Defined-Lat", lat.toString())
            put("X-User-Defined-Long", lon.toString())
            put("X-Android-Id", androidId)
            if (uuid.isNotBlank()) put("X-Zomato-UUID", uuid)
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
