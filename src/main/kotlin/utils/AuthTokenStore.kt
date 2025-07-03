package com.ark.utils

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.extensions.storeOf
import kotlinx.io.files.Path
import model.LoginTokenResp


class AuthTokenStore() {

    private val store: KStore<LoginTokenResp> = storeOf(file = Path("store/auth_tokens.json"), version = 1)

    suspend fun saveAllTokens(loginToken: LoginTokenResp) = store.set(loginToken)

    suspend fun getAllTokens() = store.get()

    suspend fun deleteAllTokens() = store.delete()
}