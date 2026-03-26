package com.simon.trackail.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.simon.trackail.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    nextDestination: String,
    onNavigate: (String) -> Unit
) {
    // 停留 2 秒后跳转到目标页面
    LaunchedEffect(Unit) {
        delay(2000L)
        onNavigate(nextDestination)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Logo 居中显示
        Image(
            painter = painterResource(id = R.drawable.vdr_logo),
            contentDescription = "Vendoroad Logo",
            modifier = Modifier.size(160.dp)
        )

        // 底部版权信息（询问展示的 @版权所有）
        Text(
            text = "© 版权所有 VENDOROAD",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}
