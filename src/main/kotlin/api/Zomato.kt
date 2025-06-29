package com.ark.api

import com.ark.zomato.ZomatoLoginFlow
import com.skydoves.sandwich.ApiResponse
import model.LoginTokenResponse
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class Zomato() : KoinComponent {

    private val loginFlow: ZomatoLoginFlow by inject()

    suspend fun sendLoginOtp(phoneNumber: String, countryId: String = "1"): ApiResponse<String> {
        try {
            loginFlow.initiateLoginFlow()

            val loginResponse = loginFlow.sendLoginOtp(phoneNumber, countryId)
            if (!loginResponse.status) return ApiResponse.Failure.Error(loginResponse.message)
            if (loginResponse.areMessageAttemptsLeft ?: true) return ApiResponse.Failure.Error(loginResponse.message)

            return ApiResponse.Success(loginResponse.message ?: "Check your text messages for otp.")
        } catch (e: Exception) {
            return ApiResponse.Failure.Exception(e)
        }
    }

    suspend fun verifyLoginOtp(
        phoneNumber: String,
        countryId: String = "1",
        otp: String
    ): ApiResponse<LoginTokenResponse> {
        try {
            val verificationResponse = loginFlow.verifyLoginOtp(phoneNumber, countryId, otp)
            if (!verificationResponse.status) return ApiResponse.Failure.Error(verificationResponse.message)
            if (!verificationResponse.hash.isNullOrBlank()) return ApiResponse.Failure.Error(
                "OTP verification failed - no valid account found with this phone number."
            )

            val approval = loginFlow.approveOtpLogin(verificationResponse.redirectTo!!)
            val token = loginFlow.getLoginToken(approval)
            if (!token.status) return ApiResponse.Failure.Exception(
                Exception("Failed to get login token")
            )
            return ApiResponse.Success(token)
        } catch (e: Exception) {
            return ApiResponse.Failure.Exception(e)
        }
    }
}