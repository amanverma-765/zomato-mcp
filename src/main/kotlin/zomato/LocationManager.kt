package com.ark.zomato

import co.touchlab.kermit.Logger
import com.ark.utils.AppConstants
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLBuilder
import io.ktor.http.headers

internal class LocationManager(private val httpClient: HttpClient) {

    val authHeaders = mapOf(
        "Accept-Encoding" to "br, gzip, deflate",
        "Host" to AppConstants.API_HOST,
    )

    suspend fun addNewLocation(
        latitude: String,
        longitude: String,
        accuracy: Double,
    ) {
        val locationUrl = URLBuilder("https://${AppConstants.API_HOST}/gw/tabbed-location").apply {
            parameters.append("android_country", "IN")
            parameters.append("lang", "en")
            parameters.append("android_language", "en")
            parameters.append("response_type", "code")
            parameters.append("city_id", "-1")
        }.buildString()

        val response = httpClient.get(locationUrl) {
            headers {
                authHeaders.forEach { (key, value) -> append(key, value) }
                append("Content-Type", "application/json; charset=UTF-8")
            }
        }

        Logger.e(response.bodyAsText())
    }

}