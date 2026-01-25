plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")   // Firebase plugin
}

android {
    namespace = "com.example.tbokhle"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.tbokhle"
        minSdk = 24
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.android.volley:volley:1.2.1")

    implementation("com.github.bumptech.glide:glide:4.16.0") // Glide to load images from URL
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0") // Glide compiler for generated API
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
    implementation("com.google.mlkit:text-recognition:16.0.1")
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
}
