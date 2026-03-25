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

import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.simon.trackail.R

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
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    // 监听导航事件
    LaunchedEffect(Unit) {
        viewModel.navigateToDashboard.collectLatest {
            onNavigateToDashboard()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.onboarding_title)) }
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
                text = stringResource(R.string.onboarding_subtitle),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 说明文字
            Text(
                text = stringResource(R.string.onboarding_description),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            TextButton(
                onClick = { uriHandler.openUri("https://api.17track.net/zh-cn") },
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Text(stringResource(R.string.how_to_get_api_key))
            }

            // API Key 输入框
            OutlinedTextField(
                value = viewModel.apiKey,
                onValueChange = { viewModel.onApiKeyChange(it) },
                label = { Text(stringResource(R.string.api_key_label)) },
                placeholder = { Text(stringResource(R.string.api_key_placeholder)) },
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
                    Text(stringResource(R.string.verify_and_save))
                }
            }
        }
    }
}
