package com.ark.zomato

import co.touchlab.kermit.Logger
import com.ark.model.ConsentApproval
import com.ark.utils.AppConstants
import com.ark.utils.generatePKCEPair
import com.ark.utils.generateState
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import model.ConsentResp
import model.LoginTokenResponse
import model.OtpLoginResp
import model.OtpVerificationResp
import model.UserInfoResp
import okhttp3.HttpUrl.Companion.toHttpUrl

internal class AuthManager(private val client: HttpClient) {
    private val pkcePair = generatePKCEPair()
    private val state = generateState()
    private var loginChallenge: String? = null

    val authHeaders = mapOf(
        "Accept-Encoding" to "br, gzip",
        "Host" to AppConstants.AUTH_HOST,
        "Cookie" to "zxcv=${pkcePair.first}; rurl=${AppConstants.REDIRECT_URI}; cid=${AppConstants.CLIENT_ID}"
    )

    suspend fun initiateLoginFlow() {
        val oauthUrl = URLBuilder("https://${AppConstants.AUTH_HOST}/oauth2/auth").apply {
            parameters.append("urlAuthorize", "https://${AppConstants.AUTH_HOST}/oauth/auth")
            parameters.append("approval_prompt", "auto")
            parameters.append("scope", "offline openid")
            parameters.append("response_type", "code")
            parameters.append("code_challenge_method", "S256")
            parameters.append("code_challenge", pkcePair.second)
            parameters.append("redirect_uri", AppConstants.REDIRECT_URI)
            parameters.append("state", state)
            parameters.append("client_id", AppConstants.CLIENT_ID)
        }.buildString()

        val response = client.get(oauthUrl) {
            headers {
                authHeaders.forEach { (key, value) -> append(key, value) }
            }
        }

        if (!response.status.isSuccess()) {
            Logger.e("Failed to initiate login flow: ${response.status.description}")
            throw IllegalStateException(response.status.description)
        }
        val reqUrl = response.request.url.toString().toHttpUrl()
        loginChallenge = reqUrl.queryParameter("login_challenge")
            ?: throw RuntimeException("Failed to get login challenge")
    }

    suspend fun sendLoginOtp(phoneNumber: String, countryId: String): OtpLoginResp {
        val loginOtpUrl = "https://accounts.zomato.com/login/phone"
        val lc = loginChallenge ?: throw IllegalStateException("Call initiateOtpLogin() first")

        val response = client.submitForm(
            url = loginOtpUrl,
            formParameters = Parameters.build {
                append("number", phoneNumber)
                append("country_id", countryId)
                append("lc", lc)
                append("type", "initiate")
                append("verification_type", "sms")
                append("package_name", "com.application.zomato")
                append("message_uuid", "")
            }
        ) {
            headers { authHeaders.forEach { (key, value) -> append(key, value) } }
        }

        if (!response.status.isSuccess()) {
            Logger.e("Failed to send OTP: ${response.status.description}")
            throw IllegalStateException(response.status.description)
        }
        return response.body<OtpLoginResp>()
    }

    suspend fun verifyLoginOtp(number: String, countryId: String, otp: String): OtpVerificationResp {
        val verifyOtpUrl = "https://accounts.zomato.com/login/phone"
        val lc = loginChallenge ?: throw IllegalStateException("Call initiateOtpLogin() first")

        val response = client.submitForm(
            url = verifyOtpUrl,
            formParameters = Parameters.build {
                append("number", number)
                append("country_id", countryId)
                append("lc", lc)
                append("type", "verify")
                append("otp", otp)
            }
        ) {
            headers { authHeaders.forEach { (key, value) -> append(key, value) } }
        }

        if (!response.status.isSuccess()) {
            Logger.e("Failed to verify OTP: ${response.status.description}")
            throw IllegalStateException(response.status.description)
        }
        return response.body<OtpVerificationResp>()
    }

    suspend fun approveOtpLogin(redirectUrl: String): ConsentApproval {
        val response = client.get(redirectUrl) {
            headers {
                authHeaders.forEach { (key, value) -> append(key, value) }
            }
        }

        if (!response.status.isSuccess()) {
            Logger.e("Failed to approve login: ${response.status.description}")
            throw IllegalStateException(response.status.description)
        }
        val reqUrl = response.request.url.toString().toHttpUrl()
        val cc = reqUrl.queryParameter("consent_challenge")
            ?: throw RuntimeException("Failed to get consent challenge")

        val consentResponse = getConsent(cc)
        if (!consentResponse.status) throw IllegalStateException("Failed to get consent response")
        if (consentResponse.redirectTo.isNullOrBlank()) throw IllegalStateException("Failed to get consent redirect url")

        return getConsentApproval(consentResponse.redirectTo)
    }

    suspend fun getLoginToken(approval: ConsentApproval): LoginTokenResponse {
        val tokenUrl = "https://accounts.zomato.com/token"

        val response = client.submitForm(
            url = tokenUrl,
            formParameters = Parameters.build {
                append("code", approval.code)
                append("state", approval.state)
                append("scope", approval.scope)
            }
        ) {
            headers {
                authHeaders.forEach { (key, value) -> append(key, value) }
            }
        }

        if (!response.status.isSuccess()) {
            Logger.e("Failed to get login token: ${response.status.description}")
            throw IllegalStateException(response.status.description)
        }
        return response.body<LoginTokenResponse>()
    }

    suspend fun getCurrentUser(): UserInfoResp {
        val userInfoUrl = "https://${AppConstants.API_HOST}/gw/user/info"
        val response = client.get(userInfoUrl) {
            headers {
                append("Content-Type", "application/json; charset=UTF-8")
                append("Accept-Encoding", "gzip, deflate, br")
            }
        }
        if (!response.status.isSuccess()) {
            Logger.e("Failed to get user info: ${response.status.description}")
            throw IllegalStateException(response.status.description)
        }
        return response.body<UserInfoResp>()
    }

    private suspend fun getConsent(consentChallenge: String): ConsentResp {
        val consentUrl = "https://accounts.zomato.com/consent"

        val response = client.submitForm(
            url = consentUrl,
            formParameters = Parameters.build {
                append("cc", consentChallenge)
            }
        ) {
            headers {
                authHeaders.forEach { (key, value) -> append(key, value) }
            }
        }

        if (!response.status.isSuccess()) {
            Logger.e("Failed to get consent: ${response.status.description}")
            throw IllegalStateException(response.status.description)
        }
        return response.body<ConsentResp>()
    }

    private suspend fun getConsentApproval(redirectUrl: String): ConsentApproval {
        val response = client.get(redirectUrl) {
            headers {
                authHeaders.forEach { (key, value) -> append(key, value) }
            }
        }

        if (!response.status.isSuccess()) {
            Logger.e("Failed to approve consent: ${response.status.description}")
            throw IllegalStateException(response.status.description)
        }
        val reqUrl = response.request.url.toString().toHttpUrl()
        val code = reqUrl.queryParameter("code") ?: throw IllegalStateException("Failed to get consent code")
        val state = reqUrl.queryParameter("state") ?: throw IllegalStateException("Failed to get consent state")
        val scope = reqUrl.queryParameter("scope") ?: throw IllegalStateException("Failed to get consent scope")
        return ConsentApproval(code, state, scope)
    }
}