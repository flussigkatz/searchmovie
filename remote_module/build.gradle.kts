plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

apply("${project.rootDir}/dependencies.gradle.kts")

android {
    compileSdk = 33
    namespace = "xyz.flussigkatz.remote_module"
}

val junit: String by extra
val extJunit: String by extra
val espresso: String by extra
val dagger2: String by extra
val dagger2Compiler: String by extra

dependencies {
    //Test
    testImplementation(junit)
    androidTestImplementation(extJunit)
    androidTestImplementation(espresso)

    //Dagger2
    implementation(dagger2)
    kapt(dagger2Compiler)

    //Retrofit
    val retrofitVersion = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")

    //okhttp3
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    implementation(project(":core:core"))
}