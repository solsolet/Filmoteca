import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    //alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
}

android {
    namespace = "es.ua.eps.filmoteca"
    compileSdk = 36 //podria baixar a 34, android 14

    defaultConfig {
        applicationId = "es.ua.eps.filmoteca"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Read MAPS_API_KEY directly from local.properties by parsing lines
        val mapsApiKey = rootProject.file("local.properties")
            .takeIf { it.exists() }
            ?.readLines()
            //noinspection WrongGradleMethod
            ?.firstOrNull { it.startsWith("MAPS_API_KEY=") }
            ?.substringAfter("=")
            ?.trim()
            ?: ""

        manifestPlaceholders["mapsApiKey"] = mapsApiKey
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17" //Do not remove this!
    }

    buildFeatures{
        compose = true
        viewBinding = true
    }
}

dependencies {
    // Dependencies de Compose (UI)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.ui)

    // Dependencies principals d'Android (Vistes i Core)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.activity)

    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.fragment)
    implementation(libs.play.services.location)

    // Dependencies de Debug (Ferramentes i Previews)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Credential Manager
    implementation(libs.androidx.credentials)
    // For devices running Android 13 (API 33) and below.
    implementation(libs.androidx.credentials.play.services.auth)
    // Google Id SDK
    implementation(libs.googleid)

    // Per a que vaja el botó Sign In de Google
    implementation(libs.play.services.auth)
    // Per als mapes
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.google.firebase.messaging)
    testImplementation(kotlin("test"))
}