package com.example.cinemiron.ui.review

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cinemiron.core.utils.K
import com.example.cinemiron.data.local.models.local.models.Review
import com.example.cinemiron.ui.search.SearchViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val reviewViewModel: ReviewViewModel = viewModel()
    val state by reviewViewModel.state.collectAsState()
    val searchViewModel: SearchViewModel = androidx.hilt.navigation.compose.hiltViewModel()

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    var reviewToDelete by remember { mutableStateOf<Review?>(null) }
    var reviewToEdit by remember { mutableStateOf<Review?>(null) }

    var selectedFilter by remember { mutableStateOf("Todas") }
    var showAddDialog by remember { mutableStateOf(false) }

    val displayedReviews by remember(state, selectedFilter) {
        derivedStateOf {
            when (selectedFilter) {
                "Mis reseñas" -> state.myReviews
                else -> state.allReviews
            }
        }
    }

    LaunchedEffect(Unit) {
        reviewViewModel.loadAllReviews()
        reviewViewModel.loadMyReviews()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(bottom = 72.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Añadir reseña"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
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
                                onLikeClick = { reviewViewModel.onLikeClicked(review) },
                                canManage = currentUserId != null && review.userId == currentUserId,
                                onEdit = { reviewToEdit = review },
                                onDelete = { reviewToDelete = review }
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
            searchViewModel = searchViewModel,
            onDismiss = { showAddDialog = false }
        )
    }

    reviewToDelete?.let { toDelete ->
        AlertDialog(
            onDismissRequest = { reviewToDelete = null },
            title = { Text("Eliminar reseña") },
            text = { Text("¿Seguro que quieres eliminar esta reseña? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        reviewViewModel.deleteReview(toDelete) {
                            reviewToDelete = null
                        }
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { reviewToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    reviewToEdit?.let { toEdit ->
        EditReviewDialog(
            review = toEdit,
            reviewViewModel = reviewViewModel,
            onDismiss = { reviewToEdit = null }
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
fun ReviewCard(
    review: Review,
    onLikeClick: () -> Unit,
    canManage: Boolean = false,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    var revealSpoiler by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

    val dateText = review.createdAt?.let {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it.toDate())
    } ?: ""

    Card(
        modifier = Modifier
            .padding(horizontal = 6.dp, vertical = 6.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            if (review.moviePosterPath.isNotEmpty()) {
                AsyncImage(
                    model = "${K.BASE_IMAGE_URL}${review.moviePosterPath}",
                    contentDescription = review.movieTitle,
                    modifier = Modifier
                        .width(70.dp)
                        .height(105.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(review.movieTitle, fontWeight = FontWeight.Bold)
                        Text(
                            "${review.userName} · $dateText",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (canManage) {
                        Box {
                            IconButton(onClick = { menuExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Filled.MoreVert,
                                    contentDescription = "Opciones de reseña"
                                )
                            }
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Editar") },
                                    onClick = {
                                        menuExpanded = false
                                        onEdit?.invoke()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Eliminar") },
                                    onClick = {
                                        menuExpanded = false
                                        onDelete?.invoke()
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RatingBar(rating = review.rating)
                    Text(
                        " ${review.rating}/10",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Text(review.reviewTitle, fontWeight = FontWeight.SemiBold)

                if (review.hasSpoiler && !revealSpoiler) {
                    Text(
                        "Spoiler - Toca para revelar",
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clickable { revealSpoiler = true },
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    Text(
                        review.reviewDescription,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    IconButton(onClick = onLikeClick, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Like",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        review.likes.toString(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
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
    movieId: Int,
    movieTitle: String,
    moviePosterPath: String,
    reviewViewModel: ReviewViewModel,
    onDismiss: () -> Unit
) {
    val state by reviewViewModel.state.collectAsState()

    var rating by remember { mutableStateOf(5f) }
    var reviewTitle by remember { mutableStateOf("") }
    var reviewDesc by remember { mutableStateOf("") }
    var spoiler by remember { mutableStateOf(false) }

    val isFormValid by remember {
        derivedStateOf {
            reviewTitle.isNotBlank() && reviewDesc.isNotBlank()
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
                Text(
                    text = movieTitle,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
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

@Composable
fun EditReviewDialog(
    review: Review,
    reviewViewModel: ReviewViewModel,
    onDismiss: () -> Unit
) {
    val state by reviewViewModel.state.collectAsState()

    var rating by remember(review.id) { mutableStateOf(review.rating.toFloat()) }
    var reviewTitle by remember(review.id) { mutableStateOf(review.reviewTitle) }
    var reviewDesc by remember(review.id) { mutableStateOf(review.reviewDescription) }
    var spoiler by remember(review.id) { mutableStateOf(review.hasSpoiler) }

    val isFormValid by remember {
        derivedStateOf {
            reviewTitle.isNotBlank() && reviewDesc.isNotBlank()
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
        title = { Text("Editar reseña", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = review.movieTitle,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
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
                    reviewViewModel.updateReview(
                        original = review,
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
                    Text("Guardar")
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

@Composable
fun AddQuickReviewDialog(
    reviewViewModel: ReviewViewModel,
    searchViewModel: SearchViewModel,
    onDismiss: () -> Unit
) {
    val state by reviewViewModel.state.collectAsState()
    val searchState by searchViewModel.searchState.collectAsState()
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
                var suggestionsExpanded by remember { mutableStateOf(false) }

                Box {
                    OutlinedTextField(
                        value = movieTitle,
                        onValueChange = {
                            movieTitle = it
                            searchViewModel.onSearchQueryChanged(it)
                            suggestionsExpanded = it.isNotBlank()
                        },
                        label = { Text("Título de la película") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    val suggestions = searchState.searchMovies
                    DropdownMenu(
                        expanded = suggestionsExpanded && suggestions.isNotEmpty(),
                        onDismissRequest = { suggestionsExpanded = false },
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        suggestions.take(10).forEach { movie ->
                            DropdownMenuItem(
                                text = { Text(movie.title) },
                                onClick = {
                                    movieTitle = movie.title
                                    movieIdText = movie.id.toString()
                                    moviePosterPath = movie.posterPath
                                    suggestionsExpanded = false
                                }
                            )
                        }
                    }
                }

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

