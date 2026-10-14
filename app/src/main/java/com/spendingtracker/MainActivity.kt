package com.spendingtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.spendingtracker.ui.MainScreen
import com.spendingtracker.ui.SettingsScreen
import com.spendingtracker.ui.theme.Background
import com.spendingtracker.ui.theme.SpendTrackTheme
import com.spendingtracker.viewmodel.MainViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            SpendTrackTheme {
                var showSplash by remember { mutableStateOf(true) }
                var currentScreen by remember { mutableStateOf("main") }

                LaunchedEffect(Unit) {
                    delay(1000)
                    showSplash = false
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Background
                ) {
                    Crossfade(
                        targetState = showSplash,
                        animationSpec = tween(400),
                        label = "SplashTransition"
                    ) { isSplashing ->
                        if (isSplashing) {
                            SplashScreenContent()
                        } else {
                            when (currentScreen) {
                                "settings" -> {
                                    SettingsScreen(
                                        onBack = { currentScreen = "main" }
                                    )
                                }
                                else -> {
                                    MainScreen(
                                        viewModel = viewModel,
                                        onNavigateToSettings = { currentScreen = "settings" }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreenContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Loading...",
            modifier = Modifier.size(200.dp)
        )
    }
}
