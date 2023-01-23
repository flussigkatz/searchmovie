@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    id("kotlin-android")
}

apply("${project.rootDir}/dependencies.gradle.kts")

android {
    compileSdk = 33
    namespace = "xyz.flussigkatz.core"
}
val junit: String by extra
val extJunit: String by extra
val espresso: String by extra

dependencies {
    //Test
    testImplementation(junit)
    androidTestImplementation(extJunit)
    androidTestImplementation(espresso)

    api(project(":core:core_api"))
    implementation(project(":core:core_impl"))
}