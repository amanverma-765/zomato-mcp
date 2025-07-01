package com.ark.di

import com.ark.utils.ZomatoHeader
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val ktorModule = module {

    singleOf(::ZomatoHeader)

    single {
        HttpClient(OkHttp) {

            install(HttpCookies) {
                storage = AcceptAllCookiesStorage()
            }

            install(HttpRedirect) {
                allowHttpsDowngrade = false
            }

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    explicitNulls = false
                    prettyPrint = true
                })
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 10_000
                socketTimeoutMillis = 10_000
                connectTimeoutMillis = 10_000
            }

            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        co.touchlab.kermit.Logger.i("[Ktor] $message")
                    }
                }
            }

            defaultRequest {
                headers {
                    get<ZomatoHeader>().getAllHeaders().forEach { (name, value) ->
                        append(name, value)
                    }
                }
            }
        }
    }
}
