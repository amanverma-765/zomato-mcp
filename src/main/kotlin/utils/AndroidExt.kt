package com.ark.utils


fun generateRandomAndroidId(): String {
    val chars = "0123456789abcdef"
    return (1..16)
        .map { chars.random() }
        .joinToString("")
}
