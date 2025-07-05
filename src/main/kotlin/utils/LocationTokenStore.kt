package com.ark.utils

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.extensions.storeOf
import kotlinx.io.files.Path
import model.LocationTokenResp

class LocationTokenStore {

    private val store: KStore<LocationTokenResp> = storeOf(file = Path("store/location_token.json"), version = 1)

    suspend fun saveLocationTokenData(locationToken: LocationTokenResp) = store.set(locationToken)

    suspend fun getLocationTokenData() = store.get()

    suspend fun deleteLocationTokenData() = store.delete()
}