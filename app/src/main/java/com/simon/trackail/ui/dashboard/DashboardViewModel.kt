package com.simon.trackail.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simon.trackail.data.local.entity.Shipment
import com.simon.trackail.data.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 首页仪表盘 ViewModel
 * 管理包裹列表显示和状态更新
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: TrackRepository
) : ViewModel() {

    /**
     * 包裹列表数据流
     */
    val shipments: StateFlow<List<Shipment>> = repository.getAllShipments()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * 切换包裹是否在自动刷新池中
     */
    fun togglePoolStatus(shipment: Shipment) {
        viewModelScope.launch {
            repository.updateShipment(shipment.copy(isInPool = !shipment.isInPool))
        }
    }

    /**
     * 删除包裹
     */
    fun deleteShipment(shipment: Shipment) {
        viewModelScope.launch {
            repository.deleteShipment(shipment)
        }
    }
}
