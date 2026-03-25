package com.simon.trackail.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.res.stringResource
import com.simon.trackail.R

/**
 * 首页仪表盘界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onShipmentClick: (Long) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val shipments by viewModel.shipments.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.dashboard_title)) },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.action_settings))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.action_add_shipment))
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
                Text(stringResource(R.string.empty_shipment_list), color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                        onDelete = { viewModel.deleteShipment(shipment) },
                        onClick = { onShipmentClick(shipment.id) }
                    )
                }
            }
        }
    }
}

/**
 * 单个物流卡片组件
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShipmentCard(
    shipment: Shipment,
    onTogglePool: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val statusColor = when (shipment.status) {
        40 -> Color(0xFF4CAF50) // 已妥投 - 绿色
        30, 35 -> Color(0xFF2196F3) // 派送中/待取货 - 蓝色
        50 -> Color(0xFFF44336) // 异常 - 红色
        else -> MaterialTheme.colorScheme.outline // 其他 - 灰色
    }

    val statusText = when (shipment.status) {
        10 -> stringResource(R.string.status_in_transit)
        20 -> stringResource(R.string.status_picked_up)
        30 -> stringResource(R.string.status_out_for_delivery)
        35 -> stringResource(R.string.status_ready_for_pickup)
        40 -> stringResource(R.string.status_delivered)
        41 -> stringResource(R.string.status_undelivered)
        50 -> stringResource(R.string.status_exception)
        60 -> stringResource(R.string.status_expired)
        else -> stringResource(R.string.status_unknown)
    }

    ElevatedCard(
        onClick = onClick,
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
                        text = "${shipment.carrierCode ?: stringResource(R.string.auto_detect)} | ${shipment.trackingNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IconButton(onClick = onTogglePool) {
                    Icon(
                        imageVector = if (shipment.isInPool) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = stringResource(R.string.auto_refresh),
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
                
                val dateFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
                val updateTime = if (shipment.lastUpdate > 0) dateFormat.format(Date(shipment.lastUpdate)) else stringResource(R.string.not_refreshed)
                Text(
                    text = stringResource(R.string.updated_at, updateTime),
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
                    Text(stringResource(R.string.action_delete))
                }
            }
        }
    }
}
