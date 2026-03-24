package com.simon.trackail.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 物流包裹实体
 * @property id 唯一标识
 * @property trackingNumber 快递单号
 * @property carrierCode 承运商代码
 * @property alias 备注名/别名
 * @property status 当前状态
 * @property isInPool 是否在聚合池中
 * @property lastUpdate 最后更新时间戳
 */
@Entity(tableName = "shipments")
data class Shipment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val trackingNumber: String,
    val carrierCode: String?,
    val alias: String?,
    val status: Int,
    val isInPool: Boolean,
    val lastEvent: String?,
    val lastUpdate: Long
)
