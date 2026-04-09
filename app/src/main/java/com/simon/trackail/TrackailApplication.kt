package com.simon.trackail

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TrackailApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // 兼容 Android 13 以下设备：主动触发 AppCompat 从持久化存储中恢复用户选择的语言
        // Android 13+ 系统会自动处理，此处调用无副作用
        AppCompatDelegate.getApplicationLocales()
    }
}
