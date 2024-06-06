plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.chtima.wallettracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.chtima.wallettracker"
        minSdk = 27
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //room
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-rxjava3:$room_version")

    // RxJava and RxAndroid
    implementation ("io.reactivex.rxjava3:rxandroid:3.0.2")

    //MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-livedata:2.4.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.0")
    implementation("androidx.lifecycle:lifecycle-reactivestreams:2.4.0")

    val autodispose = "2.2.1"
    implementation("com.uber.autodispose2:autodispose:$autodispose")
    implementation("com.uber.autodispose2:autodispose-lifecycle:$autodispose")
    implementation("com.uber.autodispose2:autodispose-android:$autodispose")
    implementation("com.uber.autodispose2:autodispose-androidx-lifecycle:$autodispose")

}