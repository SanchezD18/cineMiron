package com.example.cinemiron.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cinemiron.core.utils.K
import com.example.cinemiron.domain.models.Movie
import com.example.cinemiron.domain.models.MovieDetail

@Composable
fun FavCard(movie: MovieDetail, navController: NavController) {
    val imageUrl = "${K.BASE_IMAGE_URL}${movie.poster_path}"

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