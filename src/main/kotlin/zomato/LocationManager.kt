package com.ark.zomato

import io.ktor.client.HttpClient

class LocationManager(private val httpClient: HttpClient) {

    fun addNewLocation(
        latitude: String,
        longitude: String,
        accuracy: Double,
    ) {

    }

}