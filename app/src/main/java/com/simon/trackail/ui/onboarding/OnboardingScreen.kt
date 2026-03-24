package com.simon.trackail.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * 入站引导界面
 * 引导用户输入 17TRACK API Key 并完成初始配置
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onNavigateToDashboard: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    // 监听导航事件
    LaunchedEffect(Unit) {
        viewModel.navigateToDashboard.collectLatest {
            onNavigateToDashboard()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("欢迎使用 Trackail") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 欢迎语
            Text(
                text = "开启物流追踪之旅",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 说明文字
            Text(
                text = "本应用使用 17TRACK 提供的 API 进行物流信息查询。为了继续使用，请在下方输入您的 API Key (17token)。",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            TextButton(
                onClick = { /* TODO: 跳转到 17TRACK API 获取页面或显示更多说明 */ },
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Text("如何获取 API Key?")
            }

            // API Key 输入框
            OutlinedTextField(
                value = viewModel.apiKey,
                onValueChange = { viewModel.onApiKeyChange(it) },
                label = { Text("17TRACK API Key") },
                placeholder = { Text("请输入 17token") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                isError = viewModel.errorMessage != null,
                supportingText = {
                    if (viewModel.errorMessage != null) {
                        Text(
                            text = viewModel.errorMessage!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 验证按钮
            Button(
                onClick = { viewModel.validateAndSaveApiKey() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("验证并保存")
                }
            }
        }
    }
}
