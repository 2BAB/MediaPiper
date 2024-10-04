
import com.android.build.api.dsl.ManagedVirtualDevice
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.multiplatform) // 2.
    alias(libs.plugins.cocoapods)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.application)
    alias(libs.plugins.buildConfig)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                    freeCompilerArgs.add("-Xjdk-release=${JavaVersion.VERSION_1_8}")
                    // freeCompilerArgs.add("-XXLanguage:+ExplicitBackingFields")
                }
            }
        }
        // https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-test.html
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant {
            sourceSetTree.set(KotlinSourceSetTree.test)
            dependencies {
                debugImplementation(libs.androidx.testManifest)
                implementation(libs.androidx.junit4)
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "Mediapiper"
            isStatic = true
        }
//        it.binaries.all {
//            linkerOpts("-L/usr/lib/swift")
//            linkerOpts("-rpath", "/usr/lib/swift")
//            val aicPathSuffix = when (this.target.konanTarget) {
//                KonanTarget.IOS_ARM64 -> "ios-arm64"
//                KonanTarget.IOS_X64, KonanTarget.IOS_SIMULATOR_ARM64 -> "ios-arm64_x86_64-simulator"
//                else -> null
//            }
//            aicPathSuffix?.let { p ->
//                listOf(
//                    "MediaPipeTasksGenAIC",
//                    "MediaPipeTasksGenAI"
//                ).forEach { f ->
//                    linkerOpts("-framework", f, "-F../iosApp/Pods/$f/frameworks/$f.xcframework/$p")
//                }
//                val swiftPathSuffix = when (this.target.konanTarget) {
//                    KonanTarget.IOS_ARM64 -> "iphoneos"
//                    KonanTarget.IOS_X64, KonanTarget.IOS_SIMULATOR_ARM64 -> "iphonesimulator"
//                    else -> null
//                }
//                swiftPathSuffix?.let { sp ->
//                    val swiftPathPrefix =
//                        "/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/lib"
//                    linkerOpts("-L$swiftPathPrefix/swift/$sp")
//                    linkerOpts("-rpath", "$swiftPathPrefix/swift-5.0/$sp")
//                }
//            }
//        }
    }

    cocoapods {
        name = "Mediapiper"

        version = "1.0.2"
        ios.deploymentTarget = "15"

        summary = "Mediapiper"
        homepage = "https://github.com/2BAB/Mediapiper"

//        pod("MediaPipeTasksVision") {
//            version = "0.10.14"
//            extraOpts += listOf("-compiler-option", "-fmodules")
//        }

//        pod("MediaPipeTasksGenAIC") {
//            version = "0.10.14"
//            extraOpts += listOf("-compiler-option", "-fmodules")
//        }
//        pod("MediaPipeTasksGenAI") {
//            version = "0.10.14"
//            extraOpts += listOf("-compiler-option", "-fmodules")
//        }

    }

    sourceSets {
        all {
            languageSettings {
                optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
            }
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenmodel)
            implementation(libs.voyager.transitions)
            implementation(libs.coil)
            implementation(libs.coil.network.ktor)
            implementation(libs.napier)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.ktor.client.serialization.kotlinx.json)
//            implementation(libs.moko.permission.core)
//            implementation(libs.moko.permission.compose)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
            implementation(libs.kotlinx.coroutines.test)
        }

        androidMain.dependencies {
            implementation(compose.uiTooling)
            implementation(libs.androidx.activityCompose)
            implementation(libs.androidx.lifecycle.runtime.ktx)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.android)
            implementation(libs.kstore)
            implementation(libs.kstore.file)
            implementation(libs.mediapipe.genai.android)
            implementation(libs.mediapipe.objectdetection)
            implementation(libs.bundles.camerax)
            implementation(libs.accompanist.permission)

        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.kstore)
            implementation(libs.kstore.file)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}

android {
    namespace = "me.xx2bab.mediapiper"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 34

        applicationId = "me.xx2bab.mediapiper"
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    sourceSets["main"].apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        res.srcDirs("src/androidMain/res")
    }
    // https://developer.android.com/studio/test/gradle-managed-devices
    @Suppress("UnstableApiUsage")
    testOptions {
        managedDevices.devices {
            maybeCreate<ManagedVirtualDevice>("pixel5").apply {
                device = "Pixel 5"
                apiLevel = 34
                systemImageSource = "aosp"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        compose = true
    }
}

buildConfig {
    // BuildConfig configuration here.
    // https://github.com/gmazzo/gradle-buildconfig-plugin#usage-in-kts
    useKotlinOutput { topLevelConstants = true }
    packageName("me.xx2bab.mediapiper")
}
