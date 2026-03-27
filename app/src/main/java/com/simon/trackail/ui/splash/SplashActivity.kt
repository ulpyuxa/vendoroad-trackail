package com.simon.trackail.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.simon.trackail.MainActivity
import com.simon.trackail.R

/**
 * 启动页 Activity
 * 显示 VENDOROAD Logo，停留约1.5秒后跳转到主页面
 * 使用 noHistory=true，确保返回键不会回到启动页
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    // 启动页停留时长（毫秒）
    private val splashDurationMs = 1500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 延迟指定时间后跳转到 MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToMain()
        }, splashDurationMs)
    }

    /**
     * 跳转到主 Activity 并结束启动页
     */
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
