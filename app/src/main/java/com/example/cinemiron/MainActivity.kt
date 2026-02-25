package com.example.cinemiron

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cinemiron.ui.components.SettingsDialog
import com.example.cinemiron.ui.auth.login.LoginScreen
import com.example.cinemiron.ui.auth.register.RegisterScreen
import com.example.cinemiron.ui.auth.resetpassword.ResetPasswordScreen
import com.example.cinemiron.ui.main.MainPagerScreen
import com.example.cinemiron.ui.screens.FilmInfoAPI
import com.example.cinemiron.ui.theme.CineMironTheme
import com.example.cinemiron.ui.theme.ColorSchemeOption
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.example.cinemiron.data.local.repository.loadUserProfile
import com.example.cinemiron.data.local.repository.updateUserSettings
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        if (auth.currentUser != null && !prefs.getBoolean("remember_session", false)) {
            auth.signOut()
        }
        enableEdgeToEdge()
        setContent {
                val routeTitles = mapOf(
                    "main" to "Inicio",
                    "filminfo" to "Información",
                    "login" to "Iniciar Sesión",
                    "register" to "Registrarse"
                )
                val navController = rememberNavController()
                val currentUser = auth.currentUser

                val startDestination = when {
                    currentUser != null -> "main"
                    else -> "login"
                }
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val currentTitle = routeTitles[currentRoute] ?: "cineMirón"
                val hiddenRoutes = listOf("login", "register", "resetpassword")
                var showSettingsDialog by remember { mutableStateOf(false) }
                val systemDark = isSystemInDarkTheme()
                var isDarkTheme by remember { mutableStateOf(systemDark) }
                var selectedColorScheme by remember { mutableStateOf(ColorSchemeOption.VERDE) }
                var textScale by remember { mutableStateOf(1f) }
                var notificationsEnabled by remember { mutableStateOf(true) }
                val baseDensity = LocalDensity.current


                LaunchedEffect(currentUser?.uid) {
                    val uid = currentUser?.uid ?: return@LaunchedEffect
                    loadUserProfile(
                        userId = uid,
                        onSuccess = { profile ->
                            val settings = profile.settings
                            val tema = settings.tema.lowercase()
                            isDarkTheme = when (tema) {
                                "oscuro" -> true
                                "claro" -> false
                                else -> systemDark
                            }

                            val storedColor = settings.colorPrimario
                            selectedColorScheme = ColorSchemeOption.entries.firstOrNull {
                                it.name.equals(storedColor, ignoreCase = true)
                            } ?: ColorSchemeOption.VERDE

                            textScale = settings.textScale.coerceIn(0.8f, 1.4f)
                            notificationsEnabled = settings.notificaciones
                        },
                        onError = {
                        }
                    )
                }

                CineMironTheme(
                    darkTheme = isDarkTheme,
                    colorSchemeOption = selectedColorScheme
                ) {
                Scaffold(modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    Arrangement.SpaceBetween,
                                    Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = currentTitle,
                                        style = MaterialTheme.typography.titleLarge
                                    )

                                    if (!hiddenRoutes.contains(currentRoute)) {
                                        IconButton(onClick = { showSettingsDialog = true }) {
                                            Icon(
                                                imageVector = Icons.Filled.Settings,
                                                contentDescription = "Configuración",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }

                                if (showSettingsDialog) {
                                    SettingsDialog(
                                        onDismiss = { showSettingsDialog = false },
                                        initialDarkTheme = isDarkTheme,
                                        initialColorScheme = selectedColorScheme,
                                        initialTextScale = textScale,
                                        onThemeChanged = { newDark ->
                                            isDarkTheme = newDark
                                            val uid = auth.currentUser?.uid
                                            if (uid != null) {
                                                val tema = if (newDark) "oscuro" else "claro"
                                                val colorKey = selectedColorScheme.name
                                                updateUserSettings(
                                                    userId = uid,
                                                    tema = tema,
                                                    colorPrimario = colorKey,
                                                    textScale = textScale,
                                                    notificaciones = notificationsEnabled
                                                )
                                            }
                                        },
                                        onColorSchemeChanged = { newScheme ->
                                            selectedColorScheme = newScheme
                                            val uid = auth.currentUser?.uid
                                            if (uid != null) {
                                                val tema = if (isDarkTheme) "oscuro" else "claro"
                                                val colorKey = newScheme.name
                                                updateUserSettings(
                                                    userId = uid,
                                                    tema = tema,
                                                    colorPrimario = colorKey,
                                                    textScale = textScale,
                                                    notificaciones = notificationsEnabled
                                                )
                                            }
                                        },
                                        onTextScaleChanged = { newScale ->
                                            textScale = newScale.coerceIn(0.8f, 1.4f)
                                            val uid = auth.currentUser?.uid
                                            if (uid != null) {
                                                val tema = if (isDarkTheme) "oscuro" else "claro"
                                                val colorKey = selectedColorScheme.name
                                                updateUserSettings(
                                                    userId = uid,
                                                    tema = tema,
                                                    colorPrimario = colorKey,
                                                    textScale = textScale,
                                                    notificaciones = notificationsEnabled
                                                )
                                            }
                                        },
                                        onNotificationsChanged = { newValue ->
                                            notificationsEnabled = newValue
                                            val uid = auth.currentUser?.uid
                                            if (uid != null) {
                                                val tema = if (isDarkTheme) "oscuro" else "claro"
                                                val colorKey = selectedColorScheme.name
                                                updateUserSettings(
                                                    userId = uid,
                                                    tema = tema,
                                                    colorPrimario = colorKey,
                                                    textScale = textScale,
                                                    notificaciones = notificationsEnabled
                                                )
                                            }
                                        },
                                        onLogout = {
                                            auth.signOut()

                                            val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                                            prefs.edit().putBoolean("remember_session", false).apply()

                                            navController.navigate("login") {
                                                popUpTo(0) { inclusive = true }
                                            }
                                        }

                                    )
                                }
                            }
                        )
                    },
                    bottomBar = {},
                    floatingActionButton = {}
                ) { innerPadding ->
                    CompositionLocalProvider(
                        LocalDensity provides Density(
                            baseDensity.density,
                            baseDensity.fontScale * textScale
                        )
                    ) {
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable("login") {
                            LoginScreen(navController,
                                Modifier.padding(
                                    innerPadding
                                ), auth
                            )
                        }
                        composable("register") {
                            RegisterScreen(navController,
                                Modifier.padding(
                                    innerPadding
                                ), auth
                            )
                        }
                        composable("resetpassword") {
                            ResetPasswordScreen(navController,
                                Modifier.padding(
                                    innerPadding
                                ), auth
                            )
                        }
                        composable("main") {
                            MainPagerScreen(
                                navController = navController,
                                modifier = Modifier.padding(innerPadding),
                                auth = auth
                            )
                        }
                        composable(
                            route = "filminfo/{movieId}",
                            arguments = listOf(
                                navArgument("movieId") { type = NavType.IntType }
                            )
                        ) { backStackEntry ->
                            val movieId = backStackEntry.arguments?.getInt("movieId")
                            FilmInfoAPI(
                                navController = navController,
                                modifier = Modifier.padding(innerPadding),
                                movieId = movieId
                            )
                        }
                    }}
                }
            }
        }

    }
}

