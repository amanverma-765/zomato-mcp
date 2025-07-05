package com.ark.api

import com.ark.model.DeliveryType
import com.ark.utils.*
import com.ark.utils.utils.AddressStore
import com.ark.zomato.AuthManager
import com.ark.zomato.AddressManager
import com.skydoves.sandwich.ApiResponse
import model.AddressResp
import model.UserInfoResp
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class Zomato() : KoinComponent {

    private val authManager: AuthManager by inject()
    private val authTokenStore: AuthTokenStore by inject()
    private val sharedPref: SharedPref by inject()
    private val addressManager: AddressManager by inject()
    private val userInfoStore: UserInfoStore by inject()
    private val locationTokenStore: LocationTokenStore by inject()
    private val addressStore: AddressStore by inject()

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

    suspend fun addNewAddress(
        latitude: Double,
        longitude: Double,
        horizontalAccuracy: Int,
        deliveryType: DeliveryType,
        deliveryTypeIfOther: String = "office",
        additionalData: String
    ): ApiResponse<AddressResp> {
        try {
            // Remove address if available
            addressStore.getAddress()?.response?.successAction?.dismissAddressPage?.address?.id
                ?.let { id ->
                    addressManager.removeAddress(id.toString())
                    addressStore.deleteAddress()
                }

            val locationResp = addressManager.getLocationDetails(
                latitude = latitude,
                longitude = longitude,
                horizontalAccuracy = horizontalAccuracy
            )
            if (locationResp.status != "success") {
                return ApiResponse.Failure.Error(locationResp.subtitle ?: "Failed to get location data, try again.")
            }
            if (locationResp.location?.place?.o2Serviceablity == false) {
                return ApiResponse.Failure.Error("Zomato doesn't delivers at your location.")
            }

            var tokenResp = addressManager.getLocationToken(locationResp)
            if (tokenResp.status != "success") {
                return ApiResponse.Failure.Error("Failed to get location data, try again.")
            }
            var tokenWithAccuracy = tokenResp.copy(
                location = tokenResp.location.copy(
                    horizontalAccuracy = horizontalAccuracy
                )
            )
            locationTokenStore.saveLocationTokenData(tokenWithAccuracy)

            val userInfo = userInfoStore.getUserInfo()
            if (userInfo == null) return ApiResponse.Failure.Error("No logged in user found, Please login first.")

            val addressResp = addressManager.registerAddress(
                locationTokenResp = tokenResp,
                userInfo = userInfo,
                deliveryType = deliveryType,
                deliveryTypeIfOther = deliveryTypeIfOther,
                additionalData = additionalData
            )
            if (addressResp.status != "success") {
                return ApiResponse.Failure.Error("Failed to register your location, try again.")
            }
            if (addressResp.response.successAction?.type != "dismiss_address_page") {
                return ApiResponse.Failure.Error("Failed to register your location, try again.")
            }

            val addressId = addressResp.response.successAction.dismissAddressPage?.address?.id
            tokenResp = addressManager.getLocationToken(locationResp, addressId)
            if (tokenResp.status != "success") {
                return ApiResponse.Failure.Error("Failed to get location data, try again.")
            }
            tokenWithAccuracy = tokenResp.copy(
                location = tokenResp.location.copy(
                    horizontalAccuracy = horizontalAccuracy
                )
            )
            locationTokenStore.saveLocationTokenData(tokenWithAccuracy)
            addressStore.saveAddress(addressResp)

            return ApiResponse.Success(addressResp)
        } catch (e: Exception) {
            e.printStackTrace()
            return ApiResponse.Failure.Exception(e)
        }
    }
}