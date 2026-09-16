plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.creatorflow.ai"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.creatorflow.ai"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    val creatorFlowKeystoreFile = System.getenv("CREATORFLOW_KEYSTORE_FILE")
    val creatorFlowKeystorePassword = System.getenv("CREATORFLOW_KEYSTORE_PASSWORD")
    val creatorFlowKeyAlias = System.getenv("CREATORFLOW_KEY_ALIAS")
    val creatorFlowKeyPassword = System.getenv("CREATORFLOW_KEY_PASSWORD")

    signingConfigs {
        create("creatorFlowRelease") {
            if (
                !creatorFlowKeystoreFile.isNullOrBlank() &&
                !creatorFlowKeystorePassword.isNullOrBlank() &&
                !creatorFlowKeyAlias.isNullOrBlank() &&
                !creatorFlowKeyPassword.isNullOrBlank()
            ) {
                storeFile = file(creatorFlowKeystoreFile)
                storePassword = creatorFlowKeystorePassword
                keyAlias = creatorFlowKeyAlias
                keyPassword = creatorFlowKeyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false

            if (
                creatorFlowKeystoreFile.isNullOrBlank() ||
                creatorFlowKeystorePassword.isNullOrBlank() ||
                creatorFlowKeyAlias.isNullOrBlank() ||
                creatorFlowKeyPassword.isNullOrBlank()
            ) {
                throw GradleException(
                    "CreatorFlow release signing configuration is missing. " +
                    "Set CREATORFLOW_KEYSTORE_FILE, CREATORFLOW_KEYSTORE_PASSWORD, " +
                    "CREATORFLOW_KEY_ALIAS and CREATORFLOW_KEY_PASSWORD."
                )
            }

            if (!file(creatorFlowKeystoreFile).exists()) {
                throw GradleException(
                    "CreatorFlow release keystore not found: $creatorFlowKeystoreFile"
                )
            }

            signingConfig = signingConfigs.getByName("creatorFlowRelease")
        }

        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Jetpack Compose BOM & Core
    val composeBom = platform("androidx.compose:compose-bom:2024.09.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.5")
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // Room Database Runtime
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // Retrofit & OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // AndroidX WebKit for safe local asset loading and ES Module execution
    implementation("androidx.webkit:webkit:1.12.1")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
}
