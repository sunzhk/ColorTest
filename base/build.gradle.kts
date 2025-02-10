plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "com.sunzk.base"
    compileSdk = 34
    buildToolsVersion = "34.0.0"

    defaultConfig {
        minSdk = 23
        testOptions.targetSdk = 34
        lint.targetSdk = 34

        multiDexEnabled = true
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    viewBinding {
        enable = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment)
    implementation(libs.bundles.lifecycle)

    implementation(libs.material)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    implementation("androidx.datastore:datastore-preferences:1.0.0-alpha07")
    implementation("androidx.datastore:datastore-preferences-rxjava3:1.0.0-alpha07")
    implementation("androidx.datastore:datastore-preferences-core:1.0.0-alpha07")

    //RxJava3的依赖包
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
    //RxAndroid3的依赖包
    implementation("io.reactivex.rxjava3:rxjava:3.0.4")
    
    api("com.squareup.okhttp3:okhttp:4.9.0")
    api("com.squareup.okhttp3:logging-interceptor:4.9.0")

    implementation(libs.gson)

    api("androidx.multidex:multidex:2.0.1")
    api("com.blankj:utilcodex:1.31.0")
}