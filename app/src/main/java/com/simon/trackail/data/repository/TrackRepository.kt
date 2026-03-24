package com.simon.trackail.data.repository

import com.simon.trackail.data.local.dao.ShipmentDao
import com.simon.trackail.data.local.entity.Shipment
import com.simon.trackail.data.remote.TrackApiService
import com.simon.trackail.data.remote.model.RegisterRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 物流追踪数据仓库
 * 负责协调本地数据库和远程 API
 */
@Singleton
class TrackRepository @Inject constructor(
    private val shipmentDao: ShipmentDao,
    private val apiService: TrackApiService
) {
    /**
     * 获取所有单号的流
     */
    fun getAllShipments(): Flow<List<Shipment>> = shipmentDao.getAllShipments()

    /**
     * 根据 ID 获取单个单号详情
     */
    suspend fun getShipmentById(id: Long): Shipment? = shipmentDao.getShipmentById(id)

    /**
     * 添加或更新单号并注册到 17TRACK
     * @param token API 密钥
     * @param shipment 包裹信息
     */
    suspend fun addAndRegisterShipment(token: String, shipment: Shipment): Long {
        // 1. 调用 17TRACK API 进行注册
        try {
            val request = RegisterRequest(number = shipment.trackingNumber, carrier = shipment.carrierCode)
            apiService.register(token, listOf(request))
        } catch (e: Exception) {
            // 即使注册失败，也先保存到本地，后续重试
            e.printStackTrace()
        }
        
        // 2. 保存到本地数据库
        return shipmentDao.insertShipment(shipment)
    }

    /**
     * 更新单号（仅本地）
     */
    suspend fun updateShipment(shipment: Shipment) {
        shipmentDao.insertShipment(shipment)
    }

    /**
     * 获取刷新池中需要刷新的活跃单号
     */
    suspend fun getActivePoolShipments(): List<Shipment> = shipmentDao.getShipmentsToRefresh()

    /**
     * 删除单号
     */
    suspend fun deleteShipment(shipment: Shipment) {
        shipmentDao.deleteShipment(shipment)
    }

    /**
     * 批量更新单号状态
     * 通常在后台任务刷新后调用
     */
    suspend fun updateShipments(shipments: List<Shipment>) {
        shipments.forEach {
            shipmentDao.insertShipment(it)
        }
    }
}
