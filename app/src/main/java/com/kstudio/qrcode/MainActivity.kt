package com.kstudio.qrcode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.kstudio.qrcode.ui.theme.QrCodeTheme
import org.koin.compose.KoinContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT,),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT,)
        )
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            KoinContext {
                QrCodeTheme {
                    AppNavHost(navHostController = rememberNavController())
                }
            }
        }
    }
}
