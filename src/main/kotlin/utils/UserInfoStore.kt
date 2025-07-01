package com.ark.utils

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.extensions.storeOf
import kotlinx.io.files.Path
import model.UserInfoResp

class UserInfoStore {

    private val store: KStore<UserInfoResp> = storeOf(file = Path("store/user_info.json"), version = 1)

    suspend fun saveUserInfo(userInfoResp: UserInfoResp) = store.set(userInfoResp)

    suspend fun getUserInfo() = store.get()

    suspend fun deleteUserInfo() = store.delete()

}