package com.example.cinemiron.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cinemiron.ui.home.HomeScreen
import com.example.cinemiron.ui.profile.ProfileScreen
import com.example.cinemiron.ui.review.ReviewScreen
import com.example.cinemiron.ui.search.SearchScreen
import com.example.cinemiron.ui.theme.AccentLight
import com.example.cinemiron.ui.theme.PrimaryDark
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun MainPagerScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    auth: FirebaseAuth
) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()

    val pages = listOf(
        MainPage.Home,
        MainPage.Search,
        MainPage.Review,
        MainPage.Profile
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier.height(60.dp),
                containerColor = PrimaryDark,
                tonalElevation = 8.dp
            ) {
                pages.forEachIndexed { index, page ->
                    NavigationBarItem(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = when (page) {
                                    MainPage.Home -> Icons.Default.Home
                                    MainPage.Search -> Icons.Default.Search
                                    MainPage.Review -> Icons.Default.Edit
                                    MainPage.Profile -> Icons.Default.Person
                                },
                                contentDescription = page.label
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color(0xFFB0BEC5),
                            indicatorColor = AccentLight
                        ),
                        label = null
                    )
                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
        ) { page ->
            when (pages[page]) {
                MainPage.Home -> HomeScreen(
                    navController = navController,
                    modifier = Modifier
                )
                MainPage.Search -> SearchScreen(
                    navController = navController,
                    modifier = Modifier
                )
                MainPage.Review -> ReviewScreen(
                    navController = navController,
                    modifier = Modifier
                )
                MainPage.Profile -> ProfileScreen(
                    navController = navController,
                    modifier = Modifier,
                )
            }
        }
    }
}

private enum class MainPage(val label: String) {
    Home("Inicio"),
    Search("Buscar"),
    Review("Reseñas"),
    Profile("Perfil")
}

