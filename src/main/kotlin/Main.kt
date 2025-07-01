package com.ark

import co.touchlab.kermit.Logger
import com.ark.api.Zomato
import com.ark.di.ktorModule
import com.ark.di.mainModule
import com.skydoves.sandwich.getOrNull
import com.skydoves.sandwich.messageOrNull
import com.skydoves.sandwich.onFailure
import com.skydoves.sandwich.suspendOnSuccess
import org.koin.core.context.startKoin

suspend fun main() {
    startKoin {
        printLogger()
        modules(mainModule, ktorModule)
    }
    val zomato = Zomato()

    val user = zomato.getCurrentUser().getOrNull()
    if (user == null) login(zomato)
    else Logger.i(user.toString())
    zomato.addNewLocation(
        latitude = 28.656473,
        longitude = 77.242943,
        horizontalAccuracy = 90.0
    )
}

suspend fun login(zomato: Zomato) {
    val phone = "6386617609"
    val loginResp = zomato.sendLoginOtp(phone)
    loginResp.suspendOnSuccess {
        print("Enter your OTP: ")
        val otp = readLine()!!
        zomato.verifyLoginOtp(
            phoneNumber = phone,
            otp = otp
        ).suspendOnSuccess {
            Logger.i(this.data)
        }.onFailure {
            Logger.e(this.messageOrNull ?: "Failed to verify login otp")
        }
    }.onFailure {
        Logger.e(this.messageOrNull.toString())
    }
}