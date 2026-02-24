package com.example.cinemiron.ui.review

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cinemiron.data.local.models.local.models.Resenas
import com.example.cinemiron.data.local.repository.ResenasRepository.fakeReviews


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(navController: NavController, modifier: Modifier = Modifier, onAddClick: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Todas") }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            ReviewsHeader(
                onAddClick = { showDialog = true }
            )
            ReviewFilter(
                selected = selectedFilter,
                onSelectedChange = { selectedFilter = it }
            )
            LazyColumn {
                items(fakeReviews()) { review ->
                    ReviewCard(review)
                }
            }
        }
    }


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Añadir reseña"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ReviewFilter(
                selected = selectedFilter,
                onSelectedChange = { selectedFilter = it }
            )

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                state.error != null && displayedReviews.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error: ${state.error}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                displayedReviews.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (selectedFilter == "Mis reseñas")
                                "Aún no has escrito ninguna reseña"
                            else
                                "No hay reseñas todavía",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    LazyColumn {
                        items(displayedReviews, key = { it.id }) { review ->
                            ReviewCard(
                                review = review,
                                onLikeClick = { reviewViewModel.onLikeClicked(review) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddQuickReviewDialog(
            reviewViewModel = reviewViewModel,
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
fun ReviewFilter(selected: String, onSelectedChange: (String) -> Unit) {
    Row(modifier = Modifier.padding(8.dp)) {
        listOf("Todas", "Mis reseñas").forEach {
            FilterChip(
                selected = selected == it,
                onClick = { onSelectedChange(it) },
                label = { Text(it) },
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}

@Composable
fun ReviewCard(resena: Resenas) {
    var revealSpoiler by remember { mutableStateOf(false) }
    var likes by remember { mutableStateOf(resena.likes) }


    Card(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {


            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = resena.userImage),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp).clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(resena.userName, fontWeight = FontWeight.Bold)
                    Text(resena.date, style = MaterialTheme.typography.bodySmall)
                }
            }


            Spacer(Modifier.height(8.dp))


            Text(resena.movieTitle, fontWeight = FontWeight.Bold)


            Row(verticalAlignment = Alignment.CenterVertically) {
                RatingBar(rating = resena.rating)
                Text("${resena.rating}/10", modifier = Modifier.padding(start = 4.dp))
            }


            Text(resena.reviewTitle, fontWeight = FontWeight.SemiBold)


            if (resena.hasSpoiler && !revealSpoiler) {
                Text(
                    "Spoiler - Toca para revelar",
                    modifier = Modifier.clickable { revealSpoiler = true },
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(resena.reviewDescription)
            }


            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                IconButton(onClick = { likes++ }) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null
                    )

                }
                Text(likes.toString())
            }
        }
    }
}


@Composable
fun RatingBar(rating: Int) {
    Row {
        repeat(10) { index ->
            Icon(
                imageVector = if (index < rating) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null
            )
        }
    }
}

@Composable
fun AddReviewDialog(
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {
    var movieTitle by remember { mutableStateOf("") }
    var movieDesc by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0.0f) }
    var reviewTitle by remember { mutableStateOf("") }
    var reviewDesc by remember { mutableStateOf("") }
    var spoiler by remember { mutableStateOf(false) }

    val isFormValid by remember {
        derivedStateOf {
            movieTitle.isNotBlank() &&
                    movieDesc.isNotBlank() &&
                    reviewTitle.isNotBlank() &&
                    reviewDesc.isNotBlank()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva reseña") },
        text = {
            Column {
                OutlinedTextField(
                    value = movieTitle,
                    onValueChange = { movieTitle = it },
                    label = { Text("Título película") }
                )

                OutlinedTextField(
                    value = movieDesc,
                    onValueChange = { movieDesc = it },
                    label = { Text("Descripción película") }
                )

                Text("Valoración: ${rating.toInt()}")
                Slider(
                    value = rating,
                    onValueChange = { rating = it },
                    valueRange = 0f..10f,
                    steps = 9
                )

                OutlinedTextField(
                    value = reviewTitle,
                    onValueChange = { reviewTitle = it },
                    label = { Text("Título reseña") }
                )

                OutlinedTextField(
                    value = reviewDesc,
                    onValueChange = { reviewDesc = it },
                    label = { Text("Descripción reseña") }
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = spoiler,
                        onCheckedChange = { spoiler = it }
                    )
                    Text("Esta reseña contiene spoilers")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onSubmit,
                enabled = isFormValid
            ) {
                Text("Enviar reseña")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun AddQuickReviewDialog(
    reviewViewModel: ReviewViewModel,
    onDismiss: () -> Unit
) {
    val state by reviewViewModel.state.collectAsState()
    var movieIdText by remember { mutableStateOf("") }
    var movieTitle by remember { mutableStateOf("") }
    var moviePosterPath by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(5f) }
    var reviewTitle by remember { mutableStateOf("") }
    var reviewDesc by remember { mutableStateOf("") }
    var spoiler by remember { mutableStateOf(false) }

    val isFormValid by remember {
        derivedStateOf {
            movieTitle.isNotBlank() && reviewTitle.isNotBlank() && reviewDesc.isNotBlank()
        }
    }

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            reviewViewModel.clearSaveSuccess()
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Nueva reseña", style = MaterialTheme.typography.titleMedium)
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = movieTitle,
                    onValueChange = { movieTitle = it },
                    label = { Text("Título de la película") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = movieIdText,
                    onValueChange = { movieIdText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("ID de la película (opcional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = moviePosterPath,
                    onValueChange = { moviePosterPath = it },
                    label = { Text("Ruta del póster (opcional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                Text("Valoración: ${rating.toInt()}/10")
                Slider(
                    value = rating,
                    onValueChange = { rating = it },
                    valueRange = 0f..10f,
                    steps = 9
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = reviewTitle,
                    onValueChange = { reviewTitle = it },
                    label = { Text("Título de la reseña") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = reviewDesc,
                    onValueChange = { reviewDesc = it },
                    label = { Text("Tu opinión") },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = spoiler,
                        onCheckedChange = { spoiler = it }
                    )
                    Text("Contiene spoilers", style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val movieId = movieIdText.toIntOrNull() ?: 0
                    reviewViewModel.submitReview(
                        movieId = movieId,
                        movieTitle = movieTitle,
                        moviePosterPath = moviePosterPath,
                        rating = rating.toInt(),
                        reviewTitle = reviewTitle,
                        reviewDescription = reviewDesc,
                        hasSpoiler = spoiler
                    )
                },
                enabled = isFormValid && !state.isSaving
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                } else {
                    Text("Enviar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

