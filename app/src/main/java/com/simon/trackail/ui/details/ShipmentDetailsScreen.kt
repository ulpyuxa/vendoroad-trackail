package com.simon.trackail.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.simon.trackail.R
import com.simon.trackail.data.local.entity.Shipment
import com.simon.trackail.data.local.entity.TrackingEvent
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShipmentDetailsScreen(
    onBack: () -> Unit,
    viewModel: ShipmentDetailsViewModel = hiltViewModel()
) {
    val shipment by viewModel.shipment.collectAsState()
    val events by viewModel.events.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.shipment_details_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            shipment?.let { currentShipment ->
                ShipmentHeader(currentShipment)
            }
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            Text(
                text = stringResource(R.string.event_timeline),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (events.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_events),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(events) { event ->
                        TrackingEventItem(event)
                    }
                }
            }
        }
    }
}

@Composable
fun ShipmentHeader(shipment: Shipment) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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

            Text(
                text = "${shipment.alias ?: shipment.trackingNumber}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${shipment.carrierCode ?: stringResource(R.string.auto_detect)} | ${shipment.trackingNumber}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            SuggestionChip(
                onClick = { },
                label = { Text(statusText) }
            )
        }
    }
}

@Composable
fun TrackingEventItem(event: TrackingEvent) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val eventTimeStr = dateFormat.format(Date(event.eventTime * 1000L)) // Default to * 1000 assuming unix epoch, adjust if eventTime is ms

    Row(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(60.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = eventTimeStr,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = event.location ?: stringResource(R.string.unknown_location),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = event.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
