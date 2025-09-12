plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    alias(libs.plugins.compose.compiler)
    kotlin("plugin.serialization") version "2.0.21"
}

android {
    namespace = "com.sunzk.colortest"

    signingConfigs {
        create("keystore") {
            keyAlias = localProperties["KEY_ALIAS"].toString()
            keyPassword = localProperties["KEY_PASSWORD"].toString()
            storeFile = File(project.projectDir, localProperties["STORE_FILE"].toString())
            storePassword = localProperties["STORE_PASSWORD"].toString()
        }
    }

    compileSdk = 35
    buildToolsVersion = "34.0.0"

    defaultConfig {
        applicationId = "com.sunzk.colortest"
        minSdk = 23
        targetSdk = 34
        versionCode = 20
        versionName = "2.7.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }

    flavorDimensions.add("colorTest")
    productFlavors {
        create("local") {
            dimension = "colorTest"
            buildConfigField("boolean", "useLocalData", "true")
        }
        create("network") {
            dimension = "colorTest"
            buildConfigField("boolean", "useLocalData", "false")
        }
    }
    
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            isDebuggable = false
            isShrinkResources = false
            isJniDebuggable = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("keystore")
        }
        getByName("debug") {
            isMinifyEnabled = false
            isDebuggable = true
            signingConfig = signingConfigs.getByName("keystore")
            isShrinkResources = false
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
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.recyclerView)
    implementation(project(":base"))
    implementation(project(":compose-base"))
    testImplementation("junit:junit:4.13.2")
    testImplementation(libs.junit.jupiter)
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation(libs.junit)
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment)
    implementation(libs.bundles.lifecycle)

    implementation(libs.gson)
    implementation(libs.bundles.lottie)

    // 动态权限申请
//    implementation("com.github.tbruyelle:rxpermissions:0.12") 已过时
    // 拍照或从相册选择图片
    implementation("com.github.wildma:PictureSelector:2.1.0")
    // 显示大图，缩放、拖动
    implementation("com.shizhefei:LargeImageView:1.1.0")

    implementation(libs.bundles.datastore)
    
    implementation(libs.kotlinx.coroutines.core)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    debugImplementation(libs.compose.ui.tooling)
    
    // Navigation
    implementation(libs.bundles.androidx.navigation)
    testImplementation(libs.androidx.navigation.test)
    
    implementation(libs.okhttp3)
}
