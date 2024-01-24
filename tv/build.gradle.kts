@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinKapt)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.free.movies"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.free.movies"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        vectorDrawables {
            useSupportLibrary = true
        }

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":common"))
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(platform(libs.compose.bom))
    implementation(libs.navigation.compose)
    implementation(libs.ui.tooling.preview)
    implementation(libs.tv.foundation)
    implementation(libs.tv.material)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)

    // hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.coil)

    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}