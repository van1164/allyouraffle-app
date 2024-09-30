plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.allyouraffle.allyouraffle.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.allyouraffle.allyouraffle.android"
        minSdk = 26
        targetSdk = 34
        versionCode = 8
        versionName = "0.1.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),"proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(projects.shared)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
//    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.material3.android)
    implementation(libs.firebase.common.ktx)
//    implementation(libs.androidx.material3.android)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.androidx.material)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.play.services.auth)
    implementation(libs.androidx.animation) // Compose 애니메이션
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    // https://mvnrepository.com/artifact/com.google.firebase/firebase-messaging-ktx
    implementation("com.google.firebase:firebase-messaging-ktx:24.0.1")

    implementation(libs.play.services.ads)
    implementation(libs.accompanist.navigation.animation)
    implementation (libs.android.lottie.compose)
    implementation(libs.accompanist.permissions)

//    implementation(libs.androidx.material.pullrefresh)
}