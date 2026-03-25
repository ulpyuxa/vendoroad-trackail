package com.simon.trackail.ui.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simon.trackail.data.local.PreferenceManager
import com.simon.trackail.data.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 入站引导界面的 ViewModel
 * 负责处理 API Key 的输入、验证及保存逻辑
 */
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: TrackRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    // API Key 输入框的状态
    var apiKey by mutableStateOf("")
        private set

    // 是否正在验证中
    var isLoading by mutableStateOf(false)
        private set

    // 错误消息提示
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // 导航事件流，用于验证成功后跳转到主界面
    private val _navigateToDashboard = MutableSharedFlow<Unit>()
    val navigateToDashboard = _navigateToDashboard.asSharedFlow()

    /**
     * 更新输入的 API Key
     */
    fun onApiKeyChange(newKey: String) {
        apiKey = newKey
        errorMessage = null // 输入改变时重置错误消息
    }

    /**
     * 验证并保存 API Key
     */
    fun validateAndSaveApiKey() {
        if (apiKey.isBlank()) {
            errorMessage = "请输入 API Key"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val isValid = repository.validateApiKey(apiKey)
                if (isValid) {
                    // 验证通过，保存 Key 并触发跳转
                    preferenceManager.save17TrackApiKey(apiKey)
                    _navigateToDashboard.emit(Unit)
                } else {
                    // 验证失败
                    errorMessage = "API Key 验证失败，请检查输入是否正确"
                }
            } catch (e: Exception) {
                // 网络请求或存储异常，展示错误信息
                errorMessage = "验证出错：${e.message ?: "未知错误"}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}
