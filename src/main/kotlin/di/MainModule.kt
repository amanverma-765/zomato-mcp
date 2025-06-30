package com.ark.di

import com.ark.utils.AuthTokenStore
import com.ark.utils.SharedPref
import com.ark.utils.ZomatoHeader
import com.ark.zomato.AuthManager
import com.ark.zomato.LocationManager
import com.russhwolf.settings.Settings
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val mainModule = module {
    single<Settings> { Settings() }
    factoryOf(::AuthManager)
    singleOf(::SharedPref)
    singleOf(::AuthTokenStore)
    singleOf(::LocationManager)
}