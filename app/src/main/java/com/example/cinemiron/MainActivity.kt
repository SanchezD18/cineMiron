package com.example.cinemiron

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cinemiron.ui.components.BottomNavBar
import com.example.cinemiron.ui.components.SettingsDialog
import com.example.cinemiron.ui.screens.FilmInfo
import com.example.cinemiron.ui.screens.HomeScreen
import com.example.cinemiron.ui.screens.LoginScreen
import com.example.cinemiron.ui.screens.ProfileScreen
import com.example.cinemiron.ui.screens.RegisterScreen
import com.example.cinemiron.ui.screens.ResetPassword
import com.example.cinemiron.ui.screens.ReviewScreen
import com.example.cinemiron.ui.screens.SearchScreen
import com.example.cinemiron.ui.theme.CineMironTheme
import com.example.cinemiron.ui.theme.ColorSchemeOption
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        enableEdgeToEdge()
        setContent {
                val routeTitles = mapOf(
                    "home" to "Inicio",
                    "search" to "Buscar",
                    "popular" to "Populares",
                    "filminfo" to "Información",
                    "review" to "Reseñas",
                    "profile" to "Perfil",
                    "login" to "Iniciar Sesión",
                    "register" to "Registrarse"
                )
                val navController = rememberNavController()
                val startDestination = "login"
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val currentTitle = routeTitles[currentRoute] ?: "cineMirón"
                val hiddenRoutes = listOf("login", "register", "resetpassword")
                var showSettingsDialog by remember { mutableStateOf(false) }
                var isDarkTheme by remember { mutableStateOf(false) }
                var selectedColorScheme by remember { mutableStateOf(ColorSchemeOption.VERDE) }

                CineMironTheme(
                    darkTheme = isDarkTheme,
                    colorSchemeOption = selectedColorScheme
                ) {
                Scaffold(modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(Modifier.fillMaxWidth(),
                                    Arrangement.SpaceBetween,
                                    Alignment.CenterVertically) {
                                Text(currentTitle)
                                    if (!hiddenRoutes.contains(currentRoute)) {
                                    IconButton(
                                    onClick = {showSettingsDialog = true}
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Settings,
                                        contentDescription = "Configuración",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                } }}
                                if (showSettingsDialog) {
                                    SettingsDialog(
                                        onDismiss = { showSettingsDialog = false },
                                        initialDarkTheme = isDarkTheme,
                                        initialColorScheme = selectedColorScheme,
                                        onThemeChanged = { newValue ->
                                            isDarkTheme = newValue
                                        },
                                        onColorSchemeChanged = { newScheme ->
                                            selectedColorScheme = newScheme
                                        },
                                        onLogout = {
                                            auth.signOut()
                                            navController.navigate("login") {
                                                popUpTo(0) { inclusive = true }
                                            }
                                        }
                                    )
                                }
                                    },
                        )
                    },
                    bottomBar = {
                        if (!hiddenRoutes.contains(currentRoute)) {
                            BottomNavBar(navController = navController,
                                currentRoute)
                        }
                    },

                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        composable("login") {
                            LoginScreen(
                                navController,
                                modifier = Modifier.padding(innerPadding),
                                auth = auth
                            )
                        }
                        composable("register") {
                            RegisterScreen(
                                navController,
                                modifier = Modifier.padding(innerPadding),
                                auth
                            )
                        }
                        composable("resetpassword") {
                            ResetPassword(
                                navController,
                                modifier = Modifier.padding(innerPadding),
                                auth
                            )
                        }
                        composable("home") {
                            HomeScreen(
                                navController,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        composable("search") {
                            SearchScreen(
                                navController,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        composable("filminfo") {
                            FilmInfo(
                                navController,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        composable("review") {
                            var showDialog by remember { mutableStateOf(false) }
                            ReviewScreen(
                                navController,
                                modifier = Modifier.padding(innerPadding),
                                onAddClick = { showDialog = true }
                            )
                        }
                        composable("profile") {
                            ProfileScreen(
                                navController,
                                modifier = Modifier.padding(innerPadding),
                                auth = auth
                            )
                        }
                    }
                }
                }
            }
        }
}

