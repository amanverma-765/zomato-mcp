package com.ark.api

import com.ark.utils.AppConstants
import com.ark.utils.AuthTokenExt
import com.ark.utils.SharedPref
import com.ark.utils.generateRandomAndroidId
import com.ark.zomato.LoginManager
import com.skydoves.sandwich.ApiResponse
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

class Zomato() : KoinComponent {

    private val loginManager: LoginManager by inject()
    private val authTokenExt: AuthTokenExt by inject()
    private val sharedPref: SharedPref by inject()

    init {
        sharedPref.setString(AppConstants.ZOMATO_UUID_KEY, UUID.randomUUID().toString())
        sharedPref.setString(AppConstants.ANDROID_ID_KEY, generateRandomAndroidId())
    }

    suspend fun sendLoginOtp(phoneNumber: String, countryId: String = "1"): ApiResponse<String> {
        try {
            loginManager.initiateLoginFlow()
            val loginResponse = loginManager.sendLoginOtp(phoneNumber, countryId)

            if (!loginResponse.status) return ApiResponse.Failure.Error(loginResponse.message)
            if (loginResponse.areMsgAttemptsLeft == false) return ApiResponse.Failure.Error(loginResponse.message)

            return ApiResponse.Success(loginResponse.message ?: "Check your text messages for otp.")
        } catch (e: Exception) {
            return ApiResponse.Failure.Exception(e)
        }
    }

    suspend fun verifyLoginOtp(
        phoneNumber: String,
        countryId: String = "1",
        otp: String
    ): ApiResponse<String> {
        try {
            val verificationResponse = loginManager.verifyLoginOtp(phoneNumber, countryId, otp)
            if (!verificationResponse.status) return ApiResponse.Failure.Error(verificationResponse.message)
            if (!verificationResponse.hash.isNullOrBlank()) return ApiResponse.Failure.Error(
                "OTP verification failed - no valid account found with this phone number."
            )

            val approval = loginManager.approveOtpLogin(verificationResponse.redirectTo!!)
            val token = loginManager.getLoginToken(approval)
            if (!token.status) return ApiResponse.Failure.Exception(
                Exception("Failed to get login token")
            )
            authTokenExt.saveAllTokens(token)
            return ApiResponse.Success(verificationResponse.message)
        } catch (e: Exception) {
            return ApiResponse.Failure.Exception(e)
        }
    }
}