package com.example.cinemiron.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cinemiron.domain.models.Movie
import com.example.cinemiron.core.utils.K


@Composable
fun HomeScreen(navController: NavController, modifier: Modifier = Modifier) {
    val viewModel: HomeViewModel = hiltViewModel()
    HomeAppBarAPI(modifier, navController, viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAppBarAPI(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: HomeViewModel
) {
    Scaffold(
        modifier = modifier
    ) { innerPadding ->
        ScrollContentAPI(innerPadding, navController, viewModel)
    }
}

@Composable
fun ScrollContentAPI(
    innerPadding: PaddingValues,
    navController: NavController,
    viewModel: HomeViewModel
) {
    // Observar el estado del ViewModel
    val homeState by viewModel.homeState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Mostrar indicador de carga si está cargando
        if (homeState.isLoading && homeState.discoverMovies.isEmpty() && homeState.trendingMovies.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Mostrar error si hay alguno
        homeState.error?.let { error ->
            Text(
                text = "Error: $error",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Sección: Películas en Tendencia
        if (homeState.trendingMovies.isNotEmpty()) {
            Text(
                text = "En Tendencia",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            MovieRowAPI(movies = homeState.trendingMovies, navController = navController)
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Sección: Películas Descubiertas
        if (homeState.discoverMovies.isNotEmpty()) {
            Text(
                text = "Recomendaciones para ti",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            MovieRowAPI(movies = homeState.discoverMovies, navController = navController)
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Sección: Películas Descubiertas
        if (homeState.upcomingMovies.isNotEmpty()) {
            Text (
                text = "Próximas películas",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            MovieRowAPI(movies = homeState.upcomingMovies, navController = navController)
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Mostrar mensaje si no hay películas y no está cargando
        if (!homeState.isLoading && homeState.discoverMovies.isEmpty() && homeState.trendingMovies.isEmpty() && homeState.upcomingMovies.isEmpty() && homeState.error == null) {
            Text(
                text = "No hay películas disponibles",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun MovieRowAPI(movies: List<Movie>, navController: NavController) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        items(movies) { movie ->
            MovieItemAPI(movie = movie, navController = navController)
        }
    }
}

@Composable
fun MovieItemAPI(movie: Movie, navController: NavController) {
    val imageUrl = "${K.BASE_IMAGE_URL}${movie.posterPath}"
    
    AsyncImage(
        model = imageUrl,
        contentDescription = movie.title,
        modifier = Modifier
            .size(width = 120.dp, height = 180.dp)

            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = {
                // Navegar a la pantalla de detalles de la película con el ID
                navController.navigate("filminfo/${movie.id}")
            }),
        contentScale = ContentScale.Crop
    )
}