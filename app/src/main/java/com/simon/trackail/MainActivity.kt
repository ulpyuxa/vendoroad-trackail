package com.simon.trackail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.simon.trackail.data.local.PreferenceManager
import com.simon.trackail.ui.add.AddShipmentScreen
import com.simon.trackail.ui.dashboard.DashboardScreen
import com.simon.trackail.ui.details.ShipmentDetailsScreen
import com.simon.trackail.ui.onboarding.OnboardingScreen
import com.simon.trackail.ui.settings.SettingsScreen
import com.simon.trackail.ui.splash.SplashScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // 使用 remember 缓存启动页判断，避免重组时重复读取
            val startDestination = remember {
                try {
                    if (preferenceManager.is17TrackApiKeyConfigured()) "dashboard" else "onboarding"
                } catch (e: Exception) {
                    // PreferenceManager 初始化失败时，安全回退到引导页
                    "onboarding"
                }
            }
            TrackailApp(startDestination = startDestination)
        }
    }
}

@Composable
fun TrackailApp(startDestination: String) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(
                nextDestination = startDestination,
                onNavigate = { dest ->
                    navController.navigate(dest) {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("onboarding") {
            OnboardingScreen(
                onNavigateToDashboard = {
                    navController.navigate("dashboard") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        
        composable("dashboard") {
            DashboardScreen(
                onAddClick = { navController.navigate("add_shipment") },
                onSettingsClick = { navController.navigate("settings") },
                onShipmentClick = { shipmentId ->
                    navController.navigate("shipment_details/$shipmentId")
                }
            )
        }
        
        composable("add_shipment") {
            AddShipmentScreen(
                onBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }
        
        composable("settings") {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onLogoutClick = {
                    navController.navigate("onboarding") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("shipment_details/{shipmentId}") {
            ShipmentDetailsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
