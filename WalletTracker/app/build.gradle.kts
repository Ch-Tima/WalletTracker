plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("kapt")
}

android {
    namespace = "com.chtima.wallettracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.chtima.wallettracker"
        minSdk = 28
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.perf.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Room
    val room_version = "2.6.1"
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    kapt(libs.androidx.room.room.compiler)
    implementation(libs.androidx.room.rxjava3)

    // RxJava and RxAndroid
    implementation (libs.rxandroid)

    // MPAndroidChart
    implementation(libs.mpandroidchart)

    // Lifecycle components
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.reactivestreams)

    // AutoDispose
    implementation(libs.autodispose)
    implementation(libs.autodispose.android)
    implementation(libs.autodispose.lifecycle)
    implementation(libs.autodispose.androidx.lifecycle)
    implementation(libs.autodispose.rxlifecycle3)

    //flexbox
    implementation("com.google.android.flexbox:flexbox:3.0.0")

}