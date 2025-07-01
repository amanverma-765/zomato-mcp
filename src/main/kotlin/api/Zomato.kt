package com.ark.api

import co.touchlab.kermit.Logger
import com.ark.utils.*
import com.ark.zomato.AuthManager
import com.ark.zomato.LocationManager
import com.skydoves.sandwich.ApiResponse
import kotlinx.serialization.json.Json
import model.LocationResp
import model.UserInfoResp
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class Zomato() : KoinComponent {

    private val authManager: AuthManager by inject()
    private val authTokenStore: AuthTokenStore by inject()
    private val sharedPref: SharedPref by inject()
    private val locationManager: LocationManager by inject()
    private val userInfoStore: UserInfoStore by inject()
    private val locationDataStore: LocationDataStore by inject()

    init {
        if (sharedPref.getString(AppConstants.ZOMATO_UUID_KEY).isBlank())
            sharedPref.setString(AppConstants.ZOMATO_UUID_KEY, UUID.randomUUID().toString())
        if (sharedPref.getString(AppConstants.ANDROID_ID_KEY).isBlank())
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
            e.printStackTrace()
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
                Exception("Failed to get login token.")
            )
            authTokenStore.saveAllTokens(token)
            return ApiResponse.Success(verificationResponse.message)
        } catch (e: Exception) {
            e.printStackTrace()
            return ApiResponse.Failure.Exception(e)
        }
    }

    suspend fun getCurrentUser(): ApiResponse<UserInfoResp> {
        try {
            val userInfo = authManager.getCurrentUser()
            userInfoStore.saveUserInfo(userInfo)
            return ApiResponse.Success(userInfo)
        } catch (e: Exception) {
            e.printStackTrace()
            return ApiResponse.Failure.Exception(e)
        }
    }

    suspend fun addNewLocation(
        latitude: Double,
        longitude: Double,
        horizontalAccuracy: Double,
    ): ApiResponse<String> {
        try {
            val locationData = locationManager.getLocationDetails(
                latitude = latitude,
                longitude = longitude,
                horizontalAccuracy = horizontalAccuracy
            )
            if (locationData.status != "success") {
                return ApiResponse.Failure.Error("Failed to get location data, try again.")
            }

            val locationToken = locationManager.registerLocation(locationData)
            if (locationToken.status != "success") {
                return ApiResponse.Failure.Error("Failed to register your location, try again.")
            }
            if (!locationToken.location.place.o2Serviceablity) {
                return ApiResponse.Failure.Error("Zomato doesnt delivers at your location.")
            }
            locationDataStore.saveLocationData(locationToken)

            return ApiResponse.Success("")
        } catch (e: Exception) {
            e.printStackTrace()
            return ApiResponse.Failure.Exception(e)
        }
    }
}