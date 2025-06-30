package com.ark.utils

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.extensions.storeOf
import kotlinx.io.files.Path
import model.LoginTokenResponse


class AuthTokenStore() {

    private val store: KStore<LoginTokenResponse> = storeOf(file = Path("auth_tokens.json"), version = 1)

    suspend fun saveAllTokens(loginToken: LoginTokenResponse) = store.set(loginToken)

    suspend fun getAllTokens() = store.get()

    suspend fun deleteAllTokens() = store.delete()
}