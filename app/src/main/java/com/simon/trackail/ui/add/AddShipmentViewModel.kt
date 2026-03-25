package com.simon.trackail.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simon.trackail.data.local.PreferenceManager
import com.simon.trackail.data.local.entity.Shipment
import com.simon.trackail.data.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.simon.trackail.R

/**
 * 添加包裹 ViewModel
 */
@HiltViewModel
class AddShipmentViewModel @Inject constructor(
    private val repository: TrackRepository,
    private val preferenceManager: PreferenceManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _trackingNumber = MutableStateFlow("")
    val trackingNumber: StateFlow<String> = _trackingNumber.asStateFlow()

    private val _carrierCode = MutableStateFlow("")
    val carrierCode: StateFlow<String> = _carrierCode.asStateFlow()

    private val _alias = MutableStateFlow("")
    val alias: StateFlow<String> = _alias.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _addResult = MutableStateFlow<Boolean?>(null)
    val addResult: StateFlow<Boolean?> = _addResult.asStateFlow()

    fun onTrackingNumberChange(newValue: String) {
        _trackingNumber.value = newValue
        // 尝试自动识别单号格式（此处仅为示意逻辑，可进一步完善）
        autoDetectCarrier(newValue)
    }

    fun onCarrierCodeChange(newValue: String) {
        _carrierCode.value = newValue
    }

    fun onAliasChange(newValue: String) {
        _alias.value = newValue
    }

    private fun autoDetectCarrier(number: String) {
        // TODO: 实现更精准的单号自动识别逻辑
        // 示例：SF 开头可能为顺丰，YT 开头可能为圆通
        if (number.startsWith("SF", ignoreCase = true)) {
            _carrierCode.value = "shunfeng"
        } else if (number.startsWith("YT", ignoreCase = true)) {
            _carrierCode.value = "yuantong"
        }
    }

    fun pasteFromClipboard(text: String) {
        // 从剪贴板文本中提取可能的单号
        val possibleTrackingNumber = extractTrackingNumber(text)
        if (possibleTrackingNumber.isNotBlank()) {
            _trackingNumber.value = possibleTrackingNumber
            autoDetectCarrier(possibleTrackingNumber)
        }
    }

    private fun extractTrackingNumber(text: String): String {
        // 简单正则提取：查找 8-30 位字母数字组合
        val regex = Regex("[A-Z0-9]{8,30}", RegexOption.IGNORE_CASE)
        return regex.find(text)?.value ?: ""
    }

    fun addShipment() {
        if (_trackingNumber.value.isBlank()) return

        viewModelScope.launch {
            _isProcessing.value = true
            val token = preferenceManager.get17TrackApiKey()
            val shipment = Shipment(
                trackingNumber = _trackingNumber.value.trim(),
                carrierCode = _carrierCode.value.trim().ifBlank { null },
                alias = _alias.value.trim().ifBlank { null },
                status = 0, // 初始状态：查询中
                isInPool = true,
                lastEvent = context.getString(R.string.manually_added),
                lastUpdate = System.currentTimeMillis()
            )

            try {
                repository.addAndRegisterShipment(token, shipment)
                _addResult.value = true
            } catch (e: Exception) {
                e.printStackTrace()
                _addResult.value = false
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun resetResult() {
        _addResult.value = null
    }
}
