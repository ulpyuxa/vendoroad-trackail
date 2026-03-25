package com.simon.trackail.data.local.dao

import androidx.room.*
import com.simon.trackail.data.local.entity.Shipment
import kotlinx.coroutines.flow.Flow

/**
 * 物流包裹数据库访问对象
 */
@Dao
interface ShipmentDao {
    /**
     * 获取所有包裹的流
     */
    @Query("SELECT * FROM shipments ORDER BY lastUpdate DESC")
    fun getAllShipments(): Flow<List<Shipment>>

    /**
     * 根据 ID 获取包裹
     */
    @Query("SELECT * FROM shipments WHERE id = :id")
    suspend fun getShipmentById(id: Long): Shipment?

    /**
     * 根据单号获取包裹
     */
    @Query("SELECT * FROM shipments WHERE trackingNumber = :trackingNumber LIMIT 1")
    suspend fun getShipmentByTrackingNumber(trackingNumber: String): Shipment?

    /**
     * 插入或更新包裹
     * @return 插入后的行 ID
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShipment(shipment: Shipment): Long

    /**
     * 获取需要自动刷新的包裹列表
     * 条件：在刷新池中 (isInPool = 1) 且状态不是“已妥投” (status != 40)
     */
    @Query("SELECT * FROM shipments WHERE isInPool = 1 AND status != 40")
    suspend fun getShipmentsToRefresh(): List<Shipment>

    /**
     * 删除指定的包裹
     */
    @Delete
    suspend fun deleteShipment(shipment: Shipment)

    /**
     * 根据 ID 删除包裹
     */
    @Query("DELETE FROM shipments WHERE id = :id")
    suspend fun deleteShipmentById(id: Long)
}
