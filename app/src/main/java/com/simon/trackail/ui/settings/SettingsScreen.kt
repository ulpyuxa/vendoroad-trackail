package com.simon.trackail.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.res.stringResource
import com.simon.trackail.R

/**
 * 设置界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val exportStatus by viewModel.exportStatus.collectAsState()
    val currentInterval = viewModel.getSyncInterval()
    var selectedInterval by remember { mutableStateOf(currentInterval) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 后台同步设置
            SectionTitle(stringResource(R.string.sync_settings))
            
            Column {
                Text(stringResource(R.string.sync_interval_label), style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(4L, 8L, 12L).forEach { hours ->
                        FilterChip(
                            selected = selectedInterval == hours,
                            onClick = {
                                selectedInterval = hours
                                viewModel.updateSyncInterval(hours)
                            },
                            label = { Text(stringResource(R.string.hours_format, hours.toInt())) }
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.sync_note),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Divider()

            // 语言设置
            SectionTitle(stringResource(R.string.language_settings))

            var expandedLanguage by remember { mutableStateOf(false) }
            val currentLang = viewModel.getCurrentLanguage()
            val currentLangText = when {
                currentLang.startsWith("zh") -> stringResource(R.string.language_chinese)
                currentLang.startsWith("en") -> stringResource(R.string.language_english)
                else -> stringResource(R.string.language_system)
            }

            ExposedDropdownMenuBox(
                expanded = expandedLanguage,
                onExpandedChange = { expandedLanguage = !expandedLanguage },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = currentLangText,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLanguage) }
                )
                ExposedDropdownMenu(
                    expanded = expandedLanguage,
                    onDismissRequest = { expandedLanguage = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.language_system)) },
                        onClick = {
                            viewModel.setLanguage("system")
                            expandedLanguage = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.language_english)) },
                        onClick = {
                            viewModel.setLanguage("en")
                            expandedLanguage = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.language_chinese)) },
                        onClick = {
                            viewModel.setLanguage("zh-CN")
                            expandedLanguage = false
                        }
                    )
                }
            }

            Divider()

            // 数据导出
            SectionTitle(stringResource(R.string.data_management))
            
            OutlinedButton(
                onClick = { viewModel.exportData() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.action_export_data))
            }

            Divider()

            // 账户与 API
            SectionTitle(stringResource(R.string.account_api))
            
            Button(
                onClick = {
                    viewModel.clearApiKey()
                    onLogoutClick()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.action_clear_api_key_logout))
            }
            
            Text(
                text = stringResource(R.string.app_version_info),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
        
        // 提示信息
        exportStatus?.let { status ->
            AlertDialog(
                onDismissRequest = { viewModel.clearExportStatus() },
                title = { Text(stringResource(R.string.dialog_prompt)) },
                text = { 
                    val msg = if (status.second == null) stringResource(status.first) else stringResource(status.first, status.second!!)
                    Text(msg) 
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.clearExportStatus() }) {
                        Text(stringResource(R.string.action_confirm))
                    }
                }
            )
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
    )
}
