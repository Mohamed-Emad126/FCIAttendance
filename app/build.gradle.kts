import com.android.build.api.dsl.Packaging

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    kotlin("kapt")
    id("com.google.devtools.ksp") version "1.9.22-1.0.17"
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.memad.fciattendance"
    compileSdk = 34
    ndkVersion = "22.1.7171670"

    defaultConfig {
        applicationId = "com.memad.fciattendance"
        minSdk = 26
        targetSdk = 34
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
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    packaging {
        resources {
            excludes.add("META-INF/DEPENDENCIES")
        }
    }
    externalNativeBuild {
        ndkBuild {
            path = File("src/main/jni/Android.mk")
        }
    }

}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //coroutines
    implementation(libs.kotlinx.coroutines.core)
    //hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    // Room
    implementation(libs.androidx.room.runtime)
    // To use Kotlin Symbol Processing (KSP)
    ksp(libs.androidx.room.compiler)
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    //KTX
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.activity.ktx)
    //Navigation Component
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    //Splash Screen
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.bcprov.jdk15on)
    implementation(libs.bcpkix.jdk15on)
    implementation(libs.gson)

    implementation(libs.play.services.code.scanner)
    implementation(libs.barcode.scanning)


    implementation(libs.easypermissions)
    implementation(libs.google.api.services.sheets)
    implementation(libs.play.services.auth)

    implementation(libs.google.http.client.android)
    implementation(libs.google.api.client.android)

    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    kapt(libs.androidx.hilt.compiler)
    implementation (libs.androidx.security.crypto.ktx)

}
kapt {
    correctErrorTypes = true
}