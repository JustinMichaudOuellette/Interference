plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "ca.justinmo.interference"
    compileSdk = 37

    defaultConfig {
        applicationId = "ca.justinmo.interference"
        minSdk = 27
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        externalNativeBuild {
            cmake {
                cppFlags("-O3", "-flto", "-fvisibility=hidden")
                arguments("-DANDROID_ARM_NEON=ON", "-DANDROID_STL=c++_shared")
            }
        }
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        prefab = true
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }

    packaging {
        jniLibs {
            excludes.add("**/lib*Tests.so")
        }
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

val appcompat_version: String by rootProject.extra
val materialVersion: String by rootProject.extra
val kotlin_version: String by rootProject.extra

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.oboe)
    implementation(libs.material.v1110)
    implementation(libs.kotlin.stdlib.jdk7)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity.compose.v190)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.compose.material.icons.extended)
    
    debugImplementation(libs.androidx.ui.tooling)
}
