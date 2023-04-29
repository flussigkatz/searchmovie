@file:Suppress("UnstableApiUsage")

import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
}
apply("${project.rootDir}/dependencies.gradle.kts")

android {
    val localProperties = Properties().apply {
        file("${project.rootDir}/local.properties").let {
            if (it.canRead()) load(FileInputStream(it))
        }
    }

    signingConfigs.create("release") {
        localProperties["RELEASE_STORE_FILE"]?.let { storeFile = file(it) }
        storePassword = localProperties["RELEASE_STORE_PASSWORD"] as String
        keyAlias = localProperties["RELEASE_KEY_ALIAS"] as String
        keyPassword = localProperties["RELEASE_STORE_PASSWORD"] as String
    }
    compileSdk = 33
    buildToolsVersion = "33.0.2"

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }


    defaultConfig {
        applicationId = "xyz.flussigkatz.searchmovie"
        minSdk = 21
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            isDebuggable = false
        }
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    val api = "api"
    flavorDimensions.add(api)
    productFlavors {
        val api21 = "api21"
        val api23 = "api23"
        val api26 = "api26"
        create(api21) {
            dimension = api
            versionNameSuffix = "-$api21"
            minSdk = 21
        }
        create(api23) {
            dimension = api
            versionNameSuffix = "-$api23"
            minSdk = 23
        }
        create(api26) {
            dimension = api
            versionNameSuffix = "-$api26"
            minSdk = 26
        }
        sourceSets {
            getByName(api21).java.srcDirs("src/$api21/java")
            getByName(api23).java.srcDirs("src/$api23/java")
            getByName(api26).java.srcDirs("src/$api26/java")
            namespace = "xyz.flussigkatz.searchmovie"
        }
    }

    val junit: String by extra
    val extJunit: String by extra
    val espresso: String by extra
    val dagger2: String by extra
    val dagger2Compiler: String by extra
    dependencies {
        //Core-ktx
        implementation("androidx.core:core-ktx:1.10.0")

        //Legacy
        implementation("androidx.legacy:legacy-support-v4:1.0.0")

        //Test
        testImplementation(junit)
        androidTestImplementation(extJunit)
        androidTestImplementation(espresso)

        //Constraintlayout
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")

        //Material
        implementation("com.google.android.material:material:1.8.0")

        //AppCompat
        implementation("androidx.appcompat:appcompat:1.6.1")

        //RecyclerView
        implementation("androidx.recyclerview:recyclerview:1.3.0")

        //Lottie
        implementation("com.airbnb.android:lottie:4.1.0")

        //Navigation-ktx
        val navigationVersion = "2.5.3"
        implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
        implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")

        //Picasso
        implementation("com.squareup.picasso:picasso:2.71828")

        //Dagger2
        implementation(dagger2)
        kapt(dagger2Compiler)

        //Fragment-ktx
        implementation("androidx.fragment:fragment-ktx:1.5.7")

        //Coroutines
        val coroutinesVersion = "1.6.4"
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

        implementation(project(":core:core"))
        implementation(project(":remote_module"))

        //Firebase
        implementation(platform("com.google.firebase:firebase-bom:29.0.3"))
        implementation("com.google.firebase:firebase-analytics-ktx")
        implementation("com.google.firebase:firebase-config-ktx:21.3.0")

        //Timber
        implementation("com.jakewharton.timber:timber:5.0.1")

        //Paging
        val pagingVersion = "3.1.1"
        implementation("androidx.paging:paging-runtime-ktx:$pagingVersion")
    }
}