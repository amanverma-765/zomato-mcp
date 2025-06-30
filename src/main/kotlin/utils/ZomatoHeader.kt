package com.ark.utils

import kotlinx.coroutines.runBlocking
import java.util.*

internal class ZomatoHeader(
    private val authTokenStore: AuthTokenStore,
    private val sharedPref: SharedPref
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
        "X-Device-Width" to "1344",
        "X-Device-Height" to "2769",
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
        val lat = sharedPref.getString(AppConstants.DEVICE_LAT_KEY, "0.0")
        val lon = sharedPref.getString(AppConstants.DEVICE_LONG_KEY, "0.0")
        val uuid = sharedPref.getString(AppConstants.ZOMATO_UUID_KEY)
        val authTokens = authTokenStore.getAllTokens()
        val androidId  = sharedPref.getString(AppConstants.ANDROID_ID_KEY, "e1e55d924188ae92")

        return buildMap {
            put("X-Present-Lat", lat)
            put("X-Present-Long", lon)
            put("X-User-Defined-Lat", lat)
            put("X-User-Defined-Long", lon)
            put("X-Android-Id", androidId)
            if (uuid.isNotBlank()) put("X-Zomato-UUID", uuid)
            authTokens?.accessToken?.let { put("X-Access-Token", it) }
            authTokens?.token?.accessToken?.let { put("X-Zomato-Access-Token", it) }
        }
    }

    fun getAllHeaders(): Map<String, String> {
        return deviceInfoHeaders + staticHeaders + runBlocking { optionalHeaders() }
    }
}
