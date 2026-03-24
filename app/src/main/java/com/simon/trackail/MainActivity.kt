package com.simon.trackail

import android.os.Bundle
import androidx.activity.ComponentActivity
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
import com.simon.trackail.ui.onboarding.OnboardingScreen
import com.simon.trackail.ui.settings.SettingsScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isConfigured = preferenceManager.is17TrackApiKeyConfigured()
            val startDestination = if (isConfigured) "dashboard" else "onboarding"
            
            TrackailApp(startDestination = startDestination)
        }
    }
}

@Composable
fun TrackailApp(startDestination: String) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable("onboarding") {
            OnboardingScreen(
                onSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        
        composable("dashboard") {
            DashboardScreen(
                onAddClick = { navController.navigate("add_shipment") },
                onSettingsClick = { navController.navigate("settings") }
            )
        }
        
        composable("add_shipment") {
            AddShipmentScreen(
                onBackClick = { navController.popBackStack() },
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
    }
}
