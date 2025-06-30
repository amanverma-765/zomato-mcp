package com.ark.utils

import model.LoginTokenResponse

class AuthTokenExt(private val sharedPref: SharedPref) {
   fun saveAllTokens(loginToken: LoginTokenResponse) {
       loginToken.accessToken?.let { accessToken ->
           sharedPref.setString(AppConstants.ACCESS_TOKEN_KEY, accessToken)
       }
       loginToken.user?.id?.toString()?.let { userId ->
           sharedPref.setString(AppConstants.USER_ID_KEY, userId)
       }
       loginToken.token?.accessToken?.let { accessToken ->
           sharedPref.setString(AppConstants.ZOMATO_ACCESS_TOKEN_KEY, accessToken)
       }
       loginToken.token?.expiresAt?.let { expiresAt ->
           sharedPref.setString(AppConstants.ZOMATO_ACCESS_TOKEN_EXPIRY_KEY, expiresAt)
       }
       loginToken.token?.refreshToken?.let { refreshToken ->
           sharedPref.setString(AppConstants.REFRESH_TOKEN_KEY, refreshToken)
       }
   }

   fun getZomatoAccessToken(): String? {
       return sharedPref.getString(AppConstants.ZOMATO_ACCESS_TOKEN_KEY)
   }

   fun getUserId(): String? {
       return sharedPref.getString(AppConstants.USER_ID_KEY)
   }

   fun getAccessToken(): String? {
       return sharedPref.getString(AppConstants.ACCESS_TOKEN_KEY)
   }

   fun getAccessTokenExpiry(): String? {
       return sharedPref.getString(AppConstants.ZOMATO_ACCESS_TOKEN_EXPIRY_KEY)
   }

   fun getRefreshToken(): String? {
       return sharedPref.getString(AppConstants.REFRESH_TOKEN_KEY)
   }

}