package com.simon.trackail.data.repository

import com.simon.trackail.data.local.dao.ShipmentDao
import com.simon.trackail.data.local.entity.Shipment
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 物流追踪数据仓库
 * 负责协调本地数据库和远程 API
 */
@Singleton
class TrackRepository @Inject constructor(
    private val shipmentDao: ShipmentDao
    // private val apiService: TrackApiService // TODO: 任务 4 实现后添加
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
     * 添加或更新单号
     * 如果是新添加的单号，通常需要调用 API 进行注册
     */
    suspend fun addOrUpdateShipment(shipment: Shipment): Long {
        // TODO: 如果是新单号，在此处调用 apiService.register
        return shipmentDao.insertShipment(shipment)
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
