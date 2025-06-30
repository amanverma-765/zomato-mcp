package com.ark.api

import com.ark.utils.AppConstants
import com.ark.utils.AuthTokenStore
import com.ark.utils.SharedPref
import com.ark.utils.UserInfoStore
import com.ark.utils.generateRandomAndroidId
import com.ark.zomato.LocationManager
import com.ark.zomato.AuthManager
import com.skydoves.sandwich.ApiResponse
import model.UserInfoResp
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

class Zomato() : KoinComponent {

    private val authManager: AuthManager by inject()
    private val authTokenStore: AuthTokenStore by inject()
    private val sharedPref: SharedPref by inject()
    private val locationManager: LocationManager by inject()
    private val userInfoStore: UserInfoStore by inject()

    init {
        sharedPref.setString(AppConstants.ZOMATO_UUID_KEY, UUID.randomUUID().toString())
        sharedPref.setString(AppConstants.ANDROID_ID_KEY, generateRandomAndroidId())
    }

    suspend fun sendLoginOtp(phoneNumber: String, countryId: String = "1"): ApiResponse<String> {
        try {
            authManager.initiateLoginFlow()
            val loginResponse = authManager.sendLoginOtp(phoneNumber, countryId)

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
            val verificationResponse = authManager.verifyLoginOtp(phoneNumber, countryId, otp)
            if (!verificationResponse.status) return ApiResponse.Failure.Error(verificationResponse.message)
            if (!verificationResponse.hash.isNullOrBlank()) return ApiResponse.Failure.Error(
                "OTP verification failed - no valid account found with this phone number."
            )

            val approval = authManager.approveOtpLogin(verificationResponse.redirectTo!!)
            val token = authManager.getLoginToken(approval)
            if (!token.status) return ApiResponse.Failure.Exception(
                Exception("Failed to get login token")
            )
            authTokenStore.saveAllTokens(token)
            return ApiResponse.Success(verificationResponse.message)
        } catch (e: Exception) {
            return ApiResponse.Failure.Exception(e)
        }
    }

    suspend fun getCurrentUser(): ApiResponse<UserInfoResp> {
        try {
            val userInfo = authManager.getCurrentUser()
            userInfoStore.saveUserInfo(userInfo)
            return ApiResponse.Success(userInfo)
        } catch (e: Exception) {
            return ApiResponse.Failure.Exception(e)
        }
    }

    suspend fun addNewLocation() {
        locationManager.addNewLocation(
            latitude = 28.656473,
            longitude = 77.242943,
            accuracy = 100.0
        )
    }
}