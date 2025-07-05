package com.ark.utils.utils

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.extensions.storeOf
import kotlinx.io.files.Path
import model.AddressResp

class AddressStore {

    private val store: KStore<AddressResp> = storeOf(file = Path("store/user_address.json"), version = 1)

    suspend fun saveAddress(addressResp: AddressResp) = store.set(addressResp)

    suspend fun getAddress() = store.get()

    suspend fun deleteAddress() = store.delete()
}