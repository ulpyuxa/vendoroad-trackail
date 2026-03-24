package com.simon.trackail.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * 追踪详情事件实体
 * @property id 唯一标识
 * @property shipmentId 关联的包裹ID
 * @property eventTime 事件发生时间戳
 * @property location 事件发生地点
 * @property content 事件详细描述
 */
@Entity(
    tableName = "tracking_events",
    foreignKeys = [ForeignKey(
        entity = Shipment::class,
        parentColumns = ["id"],
        childColumns = ["shipmentId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class TrackingEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val shipmentId: Long,
    val eventTime: Long,
    val location: String?,
    val content: String
)
