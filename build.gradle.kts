plugins {
    kotlin("jvm") version "1.9.21"
    id("io.ktor.plugin") version "2.3.6"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.21"
}

group = "invest.collect.com"
version = "0.0.1"

application {
    mainClass.set("invest.collect.com.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:2.3.5")
    implementation("io.ktor:ktor-server-cors:2.3.5")
    implementation("io.ktor:ktor-server-swagger:2.3.5")
    implementation("io.ktor:ktor-server-websockets-jvm:2.3.5")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.5")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.5")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.5")
    implementation("ch.qos.logback:logback-classic:1.3.11")
    implementation("io.ktor:ktor-client-core:2.3.5")
    implementation("io.ktor:ktor-client-cio:2.3.5")
}
