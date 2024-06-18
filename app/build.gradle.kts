plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

val mainUrl: String by project
val ocrUrl: String by project

android {
    namespace = "com.bangkit.factha"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bangkit.factha"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true

        buildConfigField("String", "mainUrl", "\"$mainUrl\"")
        buildConfigField("String", "ocrUrl", "\"$ocrUrl\"")

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

    buildFeatures {
        viewBinding = true
        buildConfig = true
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

    implementation("androidx.activity:activity:1.9.0")
    implementation("androidx.fragment:fragment-ktx:1.5.1")

    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    //ui
    implementation(libs.androidx.datastore.preferences)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.0")
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.github.Ferfalk:SimpleSearchView:0.2.1")
    implementation(libs.ucrop)
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}