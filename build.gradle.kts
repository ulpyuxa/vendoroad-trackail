// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    // KSP 必须与 Hilt 在同一作用域声明，否则类加载器不同会导致构建失败
    // 参考：https://github.com/google/dagger/issues/3965
    alias(libs.plugins.ksp) apply false
}
