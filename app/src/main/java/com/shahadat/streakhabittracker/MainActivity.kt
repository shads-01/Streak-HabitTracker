package com.shahadat.streakhabittracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.shahadat.streakhabittracker.ui.navigation.NavGraph
import com.shahadat.streakhabittracker.ui.theme.AppColors
import com.shahadat.streakhabittracker.ui.theme.StreakHabitTrackerTheme
import com.shahadat.streakhabittracker.util.WallpaperManager
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StreakHabitTrackerTheme {
                val context = LocalContext.current
                val wallpaperPath by WallpaperManager.getWallpaperPath(context)
                    .collectAsState(initial = null)

                Box(modifier = Modifier.fillMaxSize()) {
                    // Wallpaper background layer (blurred)
                    if (wallpaperPath != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(File(wallpaperPath!!))
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .blur(20.dp)
                        )

                        // Dark scrim for readability
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.55f))
                        )
                    } else {
                        // Default dark background
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(AppColors.Background)
                        )
                    }

                    // App content
                    val navController = rememberNavController()
                    NavGraph(navController = navController)
                }
            }
        }
    }
}
