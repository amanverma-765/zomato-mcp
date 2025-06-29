package com.ark.di

import com.ark.zomato.ZomatoLoginFlow
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val mainModule = module {
    factoryOf(::ZomatoLoginFlow)
}