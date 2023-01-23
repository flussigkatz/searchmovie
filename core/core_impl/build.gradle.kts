plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

apply("${project.rootDir}/dependencies.gradle.kts")

android {
    compileSdk = 33
    namespace = "xyz.flussigkatz.core_impl"
    defaultConfig.javaCompileOptions.annotationProcessorOptions{
        arguments["room.schemaLocation"] = "$projectDir/schemas"
    }

}
val junit: String by extra
val extJunit: String by extra
val espresso: String by extra
val roomRuntime: String by extra
val roomKtx: String by extra
val roomPaging: String by extra
val roomCompiler: String by extra
val dagger2: String by extra
val dagger2Compiler: String by extra

dependencies {
    //Test
    testImplementation(junit)
    androidTestImplementation(extJunit)
    androidTestImplementation(espresso)

    //Room
    implementation(roomRuntime)
    implementation(roomKtx)
    implementation(roomPaging)
    kapt(roomCompiler)

    //Dagger2
    implementation(dagger2)
    kapt(dagger2Compiler)

    api(project(":core:core_api"))
}