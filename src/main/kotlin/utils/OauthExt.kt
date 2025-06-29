package com.ark.utils

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

fun generatePKCEPair(): Pair<String, String> {
    val randomBytes = ByteArray(32)
    SecureRandom().nextBytes(randomBytes)

    val codeVerifier = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes)

    val sha256 = MessageDigest.getInstance("SHA-256")
    val hashed = sha256.digest(codeVerifier.toByteArray(Charsets.US_ASCII))
    val codeChallenge = Base64.getUrlEncoder().withoutPadding().encodeToString(hashed)

    return Pair(codeVerifier, codeChallenge)
}

fun generateState(length: Int = 32): String {
    val chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    return buildString(length) {
        repeat(length) {
            append(chars.random())
        }
    }
}