plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.devden.quizly"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.devden.quizly"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)



    // Dagger hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)

    // System UI controller
    implementation(libs.accompanist.systemuicontroller)

    // Compose navigation
    implementation(libs.androidx.navigation.compose)

    // Material 3
    implementation(libs.material3)

    // Material Icons
    implementation(libs.androidx.material.icons.extended)

    // Coil
    implementation(libs.coil.compose)

    //JWT
    implementation(libs.java.jwt)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    //DataStore
    implementation (libs.androidx.datastore.preferences)

    //Paging 3
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.paging.compose)

    // Splash API
    implementation(libs.androidx.core.splashscreen)

    // Constraint layout
    implementation(libs.androidx.constraintlayout.compose)

    // Google Fonts
    implementation(libs.androidx.compose.ui.text.google.fonts)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Moshi
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.kotlin.codegen)
}