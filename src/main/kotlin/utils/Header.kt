package com.ark.utils

import java.util.UUID


internal fun getHeaders(): Map<String, String> {
    val userAgent = """
        Dalvik/2.1.0 (Linux; U; Android 14; Pixel 8 Build/UD1A.230803.041; source=android_market; 
        version=14; device_manufacturer=Google; device_brand=google; device_model=Pixel 8;
        api_version=${AppConstants.APP_VERSION}) okhttp/4.12.0""".trimIndent().replace("\n", " ")
    return mapOf(
        "User-Agent" to userAgent,
        "X-Installer-Package-Name" to "com.android.vending",
        "X-RIDER-INSTALLED" to "false",
        "X-VPN-Active" to "0",
        "X-Zomato-App-Version" to AppConstants.APP_VERSION,
        "X-Zomato-App-Version-Code" to AppConstants.APP_VERSION_CODE,
        "X-Zomato-Client-Id" to AppConstants.CLIENT_ID,
        "X-Device-Language" to "en-US",
        "X-Client-Id" to "zomato_android_v2",
        "X-APP-APPEARANCE" to "LIGHT",
        "Connection" to "keep-alive",
        "X-Zomato-Is-Metric" to "true",
        "X-Request-Id" to UUID.randomUUID().toString(),
        "X-Zomato-API-Key" to AppConstants.API_KEY,
        "X-Zomato-UUID" to "4b8a549c-5bf7-43eb-bfd3-97cc7d62a491",
        "X-Android-Id" to "e1e55d924188ae92",
        "X-DV-Token" to "DT_zR9EZtDy82y1KVreNOuZ920jHS777nxUuypPvHdFFDd"
    )
}