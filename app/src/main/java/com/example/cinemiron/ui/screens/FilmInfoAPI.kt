package com.example.cinemiron.ui.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cinemiron.tmp_ui.filminfo.FilmInfoViewModel
import com.example.cinemiron.tmp_utils.K
import com.example.cinemiron.ui.theme.Primary
import com.example.cinemiron.tmp_movie.domain.models.MovieDetail

@Composable
fun FilmInfoAPI(navController: NavController, modifier: Modifier = Modifier, movieId: Int? = null) {
    val viewModel: FilmInfoViewModel = hiltViewModel()
    val filmInfoState by viewModel.filmInfoState.collectAsStateWithLifecycle()

    LaunchedEffect(movieId) {
        movieId?.let { id ->
            viewModel.fetchMovieDetail(id)
        }
    }
    
    FilmInfoContentAPI(
        modifier = modifier,
        navController = navController,
        filmInfoState = filmInfoState
    )
}

@Composable
fun FilmInfoContentAPI(
    modifier: Modifier = Modifier,
    navController: NavController,
    filmInfoState: com.example.cinemiron.tmp_ui.filminfo.FilmInfoState
) {
    when {
        filmInfoState.isLoading && filmInfoState.movieDetail == null -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        filmInfoState.error != null -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error: ${filmInfoState.error}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        filmInfoState.movieDetail != null -> {
            TopFilmColumnAPI(
                movieDetail = filmInfoState.movieDetail!!,
                modifier = modifier
            )
        }
    }
}

@Composable
fun TopFilmColumnAPI(
    movieDetail: com.example.cinemiron.tmp_movie.domain.models.MovieDetail,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberLazyListState()
    val backdropUrl = if (!movieDetail.backdrop_path.isNullOrEmpty()) {
        "${K.BASE_IMAGE_URL}${movieDetail.backdrop_path}"
    } else null

    val backgroundColor = MaterialTheme.colorScheme.background

    Column(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.25f)
        ) {
            if (backdropUrl != null) {
                AsyncImage(
                    model = backdropUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .drawWithContent {
                            drawContent()
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Transparent,
                                        backgroundColor.copy(alpha = 0.3f),
                                        backgroundColor.copy(alpha = 0.6f),
                                        backgroundColor.copy(alpha = 0.9f),
                                        backgroundColor
                                    ),
                                    startY = 0f,
                                    endY = size.height
                                )
                            )
                        },
                    contentScale = ContentScale.Crop
                )
            }

            TopFilmInfoAPI(movieDetail)
        }

        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Presupuesto: ${movieDetail.budget} $",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                    Text(
                        text = "Recaudación: ${movieDetail.revenue} $",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                }
                DescriptionRowAPI(movieDetail.overview) }
            item {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 22.dp),
                    thickness = DividerDefaults.Thickness,
                    color = Primary
                )
            }
            item { RatingRowAPI(movieDetail.vote_average) }
        }
    }
}

@Composable
fun TopFilmInfoAPI(movieDetail: MovieDetail) {
    val imageUrl = "${K.BASE_IMAGE_URL}${movieDetail.poster_path}"
    val genresText = movieDetail.genres.joinToString(", ") { it.name }
    val year = movieDetail.release_date.take(4)
    val runtimeText = "${movieDetail.runtime} mins"

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(horizontal = 22.dp, vertical = 16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = movieDetail.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                Text(
                    text = "$year · $genresText",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    TrailerButtonAPI(
                        movieId = movieDetail.id,
                        viewModel = hiltViewModel()
                    )
                    Text(
                        text = runtimeText,
                        modifier = Modifier.padding(start = 8.dp),
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
            AsyncImage(
                model = imageUrl,
                contentDescription = movieDetail.title,
                modifier = Modifier
                    .width(120.dp)
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun TrailerButtonAPI(
    movieId: Int,
    viewModel: FilmInfoViewModel
) {
    val context = LocalContext.current

    Button(
        onClick = {
            viewModel.fetchTrailer(movieId) { key ->

                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/watch?v=$key")
                )
                context.startActivity(intent)
            }
        },
        modifier = Modifier
            .height(24.dp)
            .width(104.dp),
        contentPadding = PaddingValues(2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Text("TRAILER")
        }
    }
}


@Composable
fun DescriptionRowAPI(description: String) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        Modifier.padding(horizontal = 22.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = description.ifEmpty { "No hay descripción disponible" },
            maxLines = if (expanded) Int.MAX_VALUE else 2,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.clickable { expanded = !expanded }
        )
        if (!expanded) {
            Text(
                "Ver más...",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable { expanded = !expanded }
            )
        } else {
            Text(
                "Mostrar menos.",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable { expanded = !expanded }
            )
        }
    }

}

@Composable
fun RatingRowAPI(rating: Double) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        RatingColumnAPI(rating)
    }
}

@Composable
fun RatingColumnAPI(ratingValue: Double) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = String.format("%.1f", ratingValue),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        StarRatingBarAPI(
            rating = ratingValue / 2.0, // Convertir de 10 a escala de 5
            maxStars = 5,
            starSize = 40.dp,
            activeColor = Color.Yellow,
            inactiveColor = Color.DarkGray
        )
    }
}

@Composable
fun StarRatingBarAPI(
    rating: Double,
    maxStars: Int = 5,
    starSize: Dp = 32.dp,
    activeColor: Color = Color.Yellow,
    inactiveColor: Color = Color.Gray
) {
    Row(
        modifier = Modifier.wrapContentSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            val starRating = when {
                rating >= i -> 1.0
                rating > i - 1 -> rating - (i - 1)
                else -> 0.0
            }

            StarIconAPI(
                fillRatio = starRating,
                size = starSize,
                activeColor = activeColor,
                inactiveColor = inactiveColor
            )
        }
    }
}

@Composable
fun StarIconAPI(
    fillRatio: Double,
    size: Dp,
    activeColor: Color,
    inactiveColor: Color
) {
    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = inactiveColor,
            modifier = Modifier.size(size)
        )

        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = activeColor,
            modifier = Modifier
                .size(size)
                .drawWithContent {
                    drawIntoCanvas { canvas ->
                        canvas.save()
                        canvas.clipRect(
                            left = 0f,
                            top = 0f,
                            right = size.toPx() * fillRatio.toFloat(),
                            bottom = size.toPx()
                        )
                        drawContent()
                        canvas.restore()
                    }
                }
        )
    }
}

