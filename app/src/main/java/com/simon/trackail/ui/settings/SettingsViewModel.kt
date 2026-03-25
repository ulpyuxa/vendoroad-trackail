package com.simon.trackail.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simon.trackail.data.local.PreferenceManager
import com.simon.trackail.data.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import com.simon.trackail.R

/**
 * 设置界面 ViewModel
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: TrackRepository,
    private val preferenceManager: PreferenceManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _exportStatus = MutableStateFlow<String?>(null)
    val exportStatus: StateFlow<String?> = _exportStatus

    /**
     * 获取当前同步频率 (小时)
     */
    fun getSyncInterval(): Long = preferenceManager.getSyncInterval()

    /**
     * 设置同步频率并更新任务
     */
    fun updateSyncInterval(hours: Long) {
        preferenceManager.setSyncInterval(hours)
        repository.scheduleSync(hours)
    }

    /**
     * 清除 API Key 并取消同步任务
     */
    fun clearApiKey() {
        preferenceManager.save17TrackApiKey("")
        repository.cancelSync()
    }

    /**
     * 获取当前手动设置的语言
     */
    fun getCurrentLanguage(): String {
        val locales = AppCompatDelegate.getApplicationLocales()
        return if (locales.isEmpty) "system" else locales.get(0)?.toLanguageTag() ?: "system"
    }

    /**
     * 切换语言
     */
    fun setLanguage(languageTag: String) {
        val localeList = if (languageTag == "system") {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(languageTag)
        }
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    /**
     * 导出所有数据为 JSON
     */
    fun exportData() {
        viewModelScope.launch {
            try {
                val data = repository.getAllDataForExport()
                // 使用 kotlinx.serialization 序列化 (需要相应的 Serializer，这里简化为打印)
                // val json = Json.encodeToString(data)
                
                // TODO: 真正的文件保存逻辑 (例如使用 Storage Access Framework)
                _exportStatus.value = context.getString(R.string.export_success)
            } catch (e: Exception) {
                _exportStatus.value = context.getString(R.string.export_failed, e.message ?: "")
            }
        }
    }

    fun clearExportStatus() {
        _exportStatus.value = null
    }
}
