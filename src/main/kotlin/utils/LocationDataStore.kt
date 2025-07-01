package com.ark.utils

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.extensions.storeOf
import kotlinx.io.files.Path
import model.LocationTokenResp

class LocationDataStore {

    private val store: KStore<LocationTokenResp> = storeOf(file = Path("store/location_data.json"), version = 1)

    suspend fun saveLocationData(locationData: LocationTokenResp) = store.set(locationData)

    suspend fun getLocationData() = store.get()

    suspend fun deleteLocationData() = store.delete()
}