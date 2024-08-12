plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("kotlinx-serialization")
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(platform("io.ktor:ktor-bom:2.3.12"))
            implementation("io.ktor:ktor-client-android")
            implementation("io.ktor:ktor-client-serialization")
            implementation("io.ktor:ktor-client-logging")
            implementation("io.ktor:ktor-client-content-negotiation")
            implementation("io.ktor:ktor-serialization-kotlinx-json")
            implementation("io.ktor:ktor-client-cio")
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.androidx.lifecycle.viewmodel.compose.android)
            implementation(libs.androidx.lifecycle.viewmodel.ktx)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.ktx)
            implementation(libs.kotlinx.coroutines.android)
            //put your multiplatform dependencies here
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.allyouraffle.allyouraffle"
    compileSdk = 35
    defaultConfig {
        minSdk = 26
    }
}
