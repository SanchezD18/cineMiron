package com.example.cinemiron.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cinemiron.core.utils.K
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
                navController.navigate("filminfo/${movie.id}")
            }),
        contentScale = ContentScale.Crop
    )
}