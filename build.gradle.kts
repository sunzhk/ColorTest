// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val kotlin_version by extra("2.1.0")
    val hilt_version by extra("2.41")
    repositories {
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/repository/google")
        google()
//        jcenter()
    }
    dependencies {
        classpath(libs.gradle)
//        classpath 'org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.20'
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlin_version}")
        classpath("com.google.dagger:hilt-android-gradle-plugin:${hilt_version}")
        classpath("com.alibaba:arouter-register:1.0.2")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

plugins {
    id ("com.google.devtools.ksp") version "2.1.0-1.0.29"
    alias(libs.plugins.compose.compiler) apply false
}

allprojects {
    repositories {
        mavenCentral()
        maven ( "https://maven.aliyun.com/repository/public" )
        maven ( "https://maven.aliyun.com/repository/google" )
        maven ( "https://artifact.bytedance.com/repository/Volcengine/")
        maven ( "https://jitpack.io" )
        google()
//        jcenter()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}