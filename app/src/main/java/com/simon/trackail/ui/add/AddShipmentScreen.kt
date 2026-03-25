package com.simon.trackail.ui.add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.res.stringResource
import com.simon.trackail.R

/**
 * 添加包裹屏幕
 * 提供手动输入、剪贴板识别和扫码功能
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddShipmentScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: AddShipmentViewModel = hiltViewModel()
) {
    val trackingNumber by viewModel.trackingNumber.collectAsState()
    val carrierCode by viewModel.carrierCode.collectAsState()
    val alias by viewModel.alias.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val addResult by viewModel.addResult.collectAsState()

    val clipboardManager = LocalClipboardManager.current

    // 处理成功添加后的逻辑
    LaunchedEffect(addResult) {
        if (addResult == true) {
            onSuccess()
            viewModel.resetResult()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_shipment_title)) },
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
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 快递单号输入框
            OutlinedTextField(
                value = trackingNumber,
                onValueChange = { viewModel.onTrackingNumberChange(it) },
                label = { Text(stringResource(R.string.tracking_number_label)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.tracking_number_placeholder)) },
                trailingIcon = {
                    Row {
                        IconButton(onClick = {
                            clipboardManager.getText()?.let {
                                viewModel.pasteFromClipboard(it.text)
                            }
                        }) {
                            Icon(Icons.Default.ContentPaste, contentDescription = stringResource(R.string.action_paste))
                        }
                        IconButton(onClick = {
                            // TODO: 扫码功能占位
                        }) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = stringResource(R.string.action_scan))
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Ascii,
                    imeAction = ImeAction.Next
                )
            )

            // 承运商输入框
            OutlinedTextField(
                value = carrierCode,
                onValueChange = { viewModel.onCarrierCodeChange(it) },
                label = { Text(stringResource(R.string.carrier_code_label)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.carrier_code_placeholder)) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )

            // 备注输入框
            OutlinedTextField(
                value = alias,
                onValueChange = { viewModel.onAliasChange(it) },
                label = { Text(stringResource(R.string.alias_label)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.alias_placeholder)) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 提交按钮
            Button(
                onClick = { viewModel.addShipment() },
                modifier = Modifier.fillMaxWidth(),
                enabled = trackingNumber.isNotBlank() && !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(R.string.action_confirm_add))
                }
            }
        }
    }
}
