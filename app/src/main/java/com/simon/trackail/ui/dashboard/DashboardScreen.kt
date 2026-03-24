package com.simon.trackail.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.simon.trackail.data.local.entity.Shipment
import java.text.SimpleDateFormat
import java.util.*

/**
 * 首页仪表盘界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val shipments by viewModel.shipments.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Trackail 物流追踪") },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Refresh, contentDescription = "设置")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "添加单号")
            }
        }
    ) { padding ->
        if (shipments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("暂无追踪单号，请点击右下角添加", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(shipments, key = { it.id }) { shipment ->
                    ShipmentCard(
                        shipment = shipment,
                        onTogglePool = { viewModel.togglePoolStatus(shipment) },
                        onDelete = { viewModel.deleteShipment(shipment) }
                    )
                }
            }
        }
    }
}

/**
 * 单个物流卡片组件
 */
@Composable
fun ShipmentCard(
    shipment: Shipment,
    onTogglePool: () -> Unit,
    onDelete: () -> Unit
) {
    val statusColor = when (shipment.status) {
        40 -> Color(0xFF4CAF50) // 已妥投 - 绿色
        30, 35 -> Color(0xFF2196F3) // 派送中/待取货 - 蓝色
        50 -> Color(0xFFF44336) // 异常 - 红色
        else -> MaterialTheme.colorScheme.outline // 其他 - 灰色
    }

    val statusText = when (shipment.status) {
        10 -> "运输中"
        20 -> "已揽收"
        30 -> "派送中"
        35 -> "待取货"
        40 -> "已妥投"
        41 -> "投递失败"
        50 -> "包裹异常"
        60 -> "运输过久"
        else -> "未知状态"
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = shipment.alias ?: shipment.trackingNumber,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${shipment.carrierCode ?: "自动识别"} | ${shipment.trackingNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IconButton(onClick = onTogglePool) {
                    Icon(
                        imageVector = if (shipment.isInPool) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "自动刷新",
                        tint = if (shipment.isInPool) Color(0xFFFFB300) else MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = statusText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = statusColor
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                val dateFormat = SimpleDateFormat("MM-dd HH:mm", Locale.CHINA)
                val updateTime = if (shipment.lastUpdate > 0) dateFormat.format(Date(shipment.lastUpdate)) else "未刷新"
                Text(
                    text = "更新于: $updateTime",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!shipment.lastEvent.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = shipment.lastEvent,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("删除")
                }
            }
        }
    }
}
