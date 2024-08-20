plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("kotlinx-serialization")
}

repositories{
    google()
    mavenCentral() // Make sure this is included
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
        androidMain.dependencies {
            implementation("io.ktor:ktor-client-android:2.3.12")
        }
        iosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:2.3.12")
        }

        commonMain.dependencies {
            implementation(project.dependencies.platform("io.ktor:ktor-bom:2.3.12"))
            implementation("io.ktor:ktor-client-serialization")
            implementation("io.ktor:ktor-client-logging")
            implementation("io.ktor:ktor-client-content-negotiation")
            implementation("io.ktor:ktor-serialization-kotlinx-json")
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.play.services.auth)
            implementation(libs.androidx.lifecycle.viewmodel.ktx)
            //put your multiplatform dependencies here
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.allyouraffle.allyouraffle"
    compileSdk = 34
    defaultConfig {
        minSdk = 26
    }
}
dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.android)
}
