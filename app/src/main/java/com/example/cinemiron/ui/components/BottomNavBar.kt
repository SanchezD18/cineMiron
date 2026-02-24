package com.example.cinemiron.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cinemiron.ui.theme.AccentLight
import com.example.cinemiron.ui.theme.PrimaryDark

@Composable
fun BottomNavBar(
    navController: NavController,
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.height(60.dp),
        containerColor = PrimaryDark,
        tonalElevation = 8.dp
    ) {
        val navItems = listOf(
            NavItem("home", "Inicio", Icons.Default.Home),
            NavItem("search", "Buscar", Icons.Default.Search),
            NavItem("review", "Reseñas", Icons.Default.Edit),
            NavItem("profile", "Perfil", Icons.Default.Person)
        )
        
        navItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier
                            .size(26.dp)
                            .shadow(
                                elevation = if (currentRoute == item.route) 4.dp else 0.dp,
                                shape = CircleShape
                            )
                    )
                },
                label = null,
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color(0xFFB0BEC5),
                    indicatorColor = AccentLight
            ))
        }
    }
}

data class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)
