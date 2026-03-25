package com.simon.trackail.data.repository

import androidx.work.*
import com.simon.trackail.data.local.dao.ShipmentDao
import com.simon.trackail.data.local.dao.TrackingEventDao
import com.simon.trackail.data.local.entity.Shipment
import com.simon.trackail.data.local.entity.TrackingEvent
import com.simon.trackail.data.remote.TrackApiService
import com.simon.trackail.data.remote.model.RegisterRequest
import com.simon.trackail.data.remote.model.TrackInfoRequest
import com.simon.trackail.worker.TrackingSyncWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 物流追踪数据仓库
 * 负责协调本地数据库和远程 API
 */
@Singleton
class TrackRepository @Inject constructor(
    private val shipmentDao: ShipmentDao,
    private val trackingEventDao: TrackingEventDao,
    private val apiService: TrackApiService,
    private val workManager: WorkManager
) {
    /**
     * 获取所有数据用于导出 (JSON)
     */
    suspend fun getAllDataForExport(): Map<String, Any> {
        val shipments = shipmentDao.getAllShipments().first()
        val events = trackingEventDao.getAllEvents()
        return mapOf(
            "shipments" to shipments,
            "events" to events,
            "exportTime" to System.currentTimeMillis()
        )
    }
    companion object {
        const val SYNC_WORK_NAME = "tracking_sync_work"
    }

    /**
     * 启动定时同步任务
     * @param intervalHours 刷新频率 (小时)
     */
    fun scheduleSync(intervalHours: Long) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<TrackingSyncWorker>(
            intervalHours, TimeUnit.HOURS
        ).setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            syncRequest
        )
    }

    /**
     * 取消定时同步任务
     */
    fun cancelSync() {
        workManager.cancelUniqueWork(SYNC_WORK_NAME)
    }

    /**
     * 刷新指定的单号列表
     */
    suspend fun refreshShipments(token: String, requests: List<TrackInfoRequest>) {
        try {
            val response = apiService.getTrackInfo(token, requests)
            if (response.code == 0) {
                // TODO: 处理响应数据并更新数据库
                // 解析 response.data.accepted 里的结果
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    /**
     * 验证 API Key 是否有效
     * 通过调用 getquota 接口进行验证，避免单号识别引发的错误
     */
    suspend fun validateApiKey(token: String): Boolean {
        return try {
            val response = apiService.validateToken(token)
            // code == 0 表示 17TRACK 成功受理请求，说明 API Key 是有效的
            response.code == 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 获取所有单号的流
     */
    fun getAllShipments(): Flow<List<Shipment>> = shipmentDao.getAllShipments()

    /**
     * 根据 ID 获取单个单号详情
     */
    suspend fun getShipmentById(id: Long): Shipment? = shipmentDao.getShipmentById(id)

    /**
     * 获取指定包裹的所有追踪事件
     */
    fun getEventsByShipmentId(shipmentId: Long): Flow<List<TrackingEvent>> = trackingEventDao.getEventsByShipmentId(shipmentId)

    /**
     * 添加或更新单号并注册到 17TRACK
     * @param token API 密钥
     * @param shipment 包裹信息
     */
    suspend fun addAndRegisterShipment(token: String, shipment: Shipment): Long {
        // 1. 调用 17TRACK API 进行注册
        try {
            // carrier 字段为 17TRACK 运营商 ID (Int)，Shipment.carrierCode 为字符串代码，不直接传递
            // 传入 null 让 API 自动识别运营商
            val request = RegisterRequest(number = shipment.trackingNumber)
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
