package com.simon.trackail.data.local.dao

import androidx.room.*
import com.simon.trackail.data.local.entity.TrackingEvent
import kotlinx.coroutines.flow.Flow

/**
 * 追踪详情事件数据库访问对象
 */
@Dao
interface TrackingEventDao {
    /**
     * 获取指定包裹的所有详情事件
     * @param shipmentId 包裹 ID
     */
    @Query("SELECT * FROM tracking_events WHERE shipmentId = :shipmentId ORDER BY eventTime DESC")
    fun getEventsByShipmentId(shipmentId: Long): Flow<List<TrackingEvent>>

    /**
     * 同步获取指定包裹的所有详情事件
     */
    @Query("SELECT * FROM tracking_events WHERE shipmentId = :shipmentId ORDER BY eventTime DESC")
    suspend fun getEventsByShipmentIdSync(shipmentId: Long): List<TrackingEvent>

    /**
     * 批量插入追踪事件
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<TrackingEvent>)

    /**
     * 删除指定包裹的所有追踪事件
     */
    @Query("DELETE FROM tracking_events WHERE shipmentId = :shipmentId")
    suspend fun deleteEventsByShipmentId(shipmentId: Long)

    /**
     * 获取所有追踪事件（用于数据导出）
     */
    @Query("SELECT * FROM tracking_events")
    suspend fun getAllEvents(): List<TrackingEvent>
}
