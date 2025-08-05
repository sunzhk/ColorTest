plugins {
	id("com.android.library")
	id("kotlin-android")
	alias(libs.plugins.compose.compiler)
}

android {
	namespace = "com.sunzk.compose.base"
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

	buildFeatures {
		buildConfig = true
		compose = true
	}

}

dependencies {
	implementation(project(":base"))

	implementation(libs.utilcodex)
	implementation(libs.material)
	// Compose
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.bundles.compose)
	debugImplementation(libs.compose.ui.tooling)
}