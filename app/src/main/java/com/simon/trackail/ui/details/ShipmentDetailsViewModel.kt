package com.simon.trackail.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simon.trackail.data.local.PreferenceManager
import com.simon.trackail.data.local.entity.Shipment
import com.simon.trackail.data.local.entity.TrackingEvent
import com.simon.trackail.data.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShipmentDetailsViewModel @Inject constructor(
    private val repository: TrackRepository,
    private val preferenceManager: PreferenceManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Retrieve shipmentId passed via Navigation arguments
    val shipmentId: Long = savedStateHandle.get<String>("shipmentId")?.toLongOrNull() ?: -1L

    private val _shipment = MutableStateFlow<Shipment?>(null)
    val shipment: StateFlow<Shipment?> = _shipment.asStateFlow()

    private val _events = MutableStateFlow<List<TrackingEvent>>(emptyList())
    val events: StateFlow<List<TrackingEvent>> = _events.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        if (shipmentId != -1L) {
            loadShipmentDetails()
            observeTrackingEvents()
        }
    }

    private fun loadShipmentDetails() {
        viewModelScope.launch {
            val shipmentDetail = repository.getShipmentById(shipmentId)
            _shipment.value = shipmentDetail
            
            // Trigger API refresh
            shipmentDetail?.let {
                val token = preferenceManager.get17TrackApiKey()
                if (token.isNotBlank()) {
                    val req = com.simon.trackail.data.remote.model.TrackInfoRequest(
                        number = it.trackingNumber,
                        carrier = it.carrierCode?.toIntOrNull()
                    )
                    repository.refreshShipments(token, listOf(req))
                }
            }
        }
    }

    private fun observeTrackingEvents() {
        viewModelScope.launch {
            repository.getEventsByShipmentId(shipmentId).collect { eventList ->
                _events.value = eventList
            }
        }
    }

    /**
     * 手动触发实时查询 (GetRealTimeTrackInfo)
     */
    fun forceRefreshRealTime() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val shipmentDetail = repository.getShipmentById(shipmentId)
                shipmentDetail?.let {
                    val token = preferenceManager.get17TrackApiKey()
                    if (token.isNotBlank()) {
                        val req = com.simon.trackail.data.remote.model.TrackInfoRequest(
                            number = it.trackingNumber,
                            carrier = it.carrierCode?.toIntOrNull()
                        )
                        repository.refreshRealTimeShipment(token, listOf(req))
                    }
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
