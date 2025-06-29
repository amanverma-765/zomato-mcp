plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.shadow)
    application
}

group = "com.ark"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("com.ark.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.sandwich)
    implementation(libs.sandwich.ktor)
    implementation(libs.modelcontext.sdk)
    implementation(libs.slf4j.nop)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kermit)
    implementation(libs.koin.core)
    implementation("io.ktor:ktor-client-okhttp-jvm:3.1.3")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}