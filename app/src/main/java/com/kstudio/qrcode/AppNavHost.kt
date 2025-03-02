package com.kstudio.qrcode

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kstudio.qrcode.features.generate.GenerateScreen
import com.kstudio.qrcode.features.history.HistoryScreen
import com.kstudio.qrcode.features.scan.ScanScreen

val LocalNavController = staticCompositionLocalOf<NavHostController?> { null }

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    startDestination: String = NavigationItem.Home.route
) {
    CompositionLocalProvider(LocalNavController provides navHostController) {
        NavHost(navHostController, startDestination, modifier = modifier) {
            composable(NavigationItem.Home.route) {
                ScanScreen()
            }
            composable(NavigationItem.Generate.route) {
                GenerateScreen()
            }
            composable(NavigationItem.History.route) {
                HistoryScreen()
            }
            composable(NavigationItem.Setting.route) { }
        }
    }
}
