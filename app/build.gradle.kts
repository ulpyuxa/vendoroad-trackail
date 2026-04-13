plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.serialization)
    // 使用 KSP 替代 kapt，完全兼容 Kotlin 2.x
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.simon.trackail"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.simon.trackail"
        minSdk = 26
        targetSdk = 35
        versionCode = 10103  //10101 内部测试，10102 封闭式测试， 10103 开放式测试
        versionName = "1.1.3"
        resourceConfigurations.addAll(listOf("en", "zh"))

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // 签名配置：从环境变量读取，适配 GitHub Actions CI 环境
    signingConfigs {
        getByName("debug") {
            // CI 环境：GitHub Actions 将 keystore 解码到临时文件后，通过环境变量传入路径
            val debugKeystorePath = System.getenv("DEBUG_KEYSTORE_PATH")
            if (debugKeystorePath != null) {
                storeFile = file(debugKeystorePath)
                storePassword = System.getenv("DEBUG_KEYSTORE_PASSWORD") ?: ""
                keyAlias = System.getenv("DEBUG_KEY_ALIAS") ?: ""
                keyPassword = System.getenv("DEBUG_KEY_PASSWORD") ?: ""
            }
        }
        create("release") {
            // CI 环境：GitHub Actions 将正式 keystore 解码到临时文件后，通过环境变量传入路径
            val releaseKeystorePath = System.getenv("RELEASE_KEYSTORE_PATH")
            if (releaseKeystorePath != null) {
                storeFile = file(releaseKeystorePath)
                storePassword = System.getenv("RELEASE_KEYSTORE_PASSWORD") ?: ""
                keyAlias = System.getenv("RELEASE_KEY_ALIAS") ?: ""
                keyPassword = System.getenv("RELEASE_KEY_PASSWORD") ?: ""
            }
        }
    }

    buildTypes {
        debug {
            // 使用环境变量中的固定签名，确保每次 CI 构建签名一致，支持覆盖安装
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            // 使用正式 Release 签名，用于上架 Google Play
            signingConfig = signingConfigs.getByName("release")
            // 启用代码混淆与资源压缩，减小包体积
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
}

// 顶层 kotlin 块，用于配置 Kotlin 编译器选项（替代废弃的 kotlinOptions）
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}


dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.material)
    
    // Room - 使用 ksp 替代 kapt 处理注解
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    
    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.okhttp.logging.interceptor)
    
    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)
    
    // Hilt - 使用 ksp 替代 kapt 处理注解
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
    // Hilt Navigation Compose：提供 hiltViewModel() 函数
    implementation(libs.androidx.hilt.navigation.compose)
    // Material Icons Extended：提供 ContentPaste、QrCodeScanner 等扩展图标
    implementation(libs.androidx.compose.material.icons.extended)
    
    // Serialization
    implementation(libs.kotlinx.serialization.json)
    
    // Security
    implementation(libs.androidx.security.crypto)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
