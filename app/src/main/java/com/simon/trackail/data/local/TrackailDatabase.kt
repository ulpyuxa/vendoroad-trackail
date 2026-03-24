package com.simon.trackail.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.simon.trackail.data.local.dao.ShipmentDao
import com.simon.trackail.data.local.dao.TrackingEventDao
import com.simon.trackail.data.local.entity.Shipment
import com.simon.trackail.data.local.entity.TrackingEvent

/**
 * 数据库定义类
 * 包含包裹数据和详情追踪事件数据
 */
@Database(entities = [Shipment::class, TrackingEvent::class], version = 1)
abstract class TrackailDatabase : RoomDatabase() {
    abstract fun shipmentDao(): ShipmentDao
    abstract fun trackingEventDao(): TrackingEventDao
}
