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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cinemiron.components.BottomNavBar
import com.example.cinemiron.screens.FimlInfo
import com.example.cinemiron.screens.HomeScreen
import com.example.cinemiron.screens.LoginScreen
import com.example.cinemiron.screens.Profile
import com.example.cinemiron.screens.RegisterScreen
import com.example.pruebas_apis.ui.theme.CineMironTheme


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CineMironTheme {
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
                val hiddenRoutes = listOf("login", "register")

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
                                    onClick = { /*TODO*/ }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Settings,
                                        contentDescription = "Configuración",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                } }} },

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
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        composable("register") {
                            RegisterScreen(
                                navController,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        composable("home") {
                            HomeScreen(
                                navController,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        composable("search") {
                            Profile(
                                navController,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        composable("popular") {
                            Profile(
                                navController,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        composable("filminfo") {
                            FimlInfo(
                                navController,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        composable("review") {
                            Profile(
                                navController,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        composable("profile") {
                            Profile(
                                navController,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}

