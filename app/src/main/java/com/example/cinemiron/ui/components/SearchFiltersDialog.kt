package com.example.cinemiron.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

enum class SortOption {
    POPULARIDAD,
    RATING,
    ALFABETICO
}

enum class ContentType {
    TODOS,
    PELICULAS,
    SERIES
}

@Composable
fun SearchFiltersDialog(
    onDismiss: () -> Unit,
    onApplyFilters: (
        sortOption: SortOption,
        selectedGenres: List<String>,
        contentType: ContentType,
        onlyFriends: Boolean
    ) -> Unit
) {
    var sortOption by remember { mutableStateOf(SortOption.POPULARIDAD) }
    var selectedGenres by remember { mutableStateOf<List<String>>(emptyList()) }
    var contentType by remember { mutableStateOf(ContentType.TODOS) }
    var onlyFriends by remember { mutableStateOf(false) }

    val genres = listOf(
        "Acción", "Aventura", "Comedia", "Drama", "Terror",
        "Ciencia Ficción", "Romance", "Thriller", "Animación", "Documental"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Filtros de Búsqueda",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Ordenar por",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Column(modifier = Modifier.selectableGroup()) {
                    SortOption.entries.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (sortOption == option),
                                    onClick = { sortOption = option },
                                    role = Role.RadioButton
                                )
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (sortOption == option),
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (option) {
                                    SortOption.POPULARIDAD -> "Popularidad (más favoritos)"
                                    SortOption.RATING -> "Rating"
                                    SortOption.ALFABETICO -> "Alfabéticamente"
                                },
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Text(
                    text = "Géneros",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    genres.chunked(2).forEach { genreRow ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            genreRow.forEach { genre ->
                                GenreChip(
                                    genre = genre,
                                    selected = selectedGenres.contains(genre),
                                    onClick = {
                                        selectedGenres = if (selectedGenres.contains(genre)) {
                                            selectedGenres - genre
                                        } else {
                                            selectedGenres + genre
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (genreRow.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Text(
                    text = "Tipo de contenido",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Column(modifier = Modifier.selectableGroup()) {
                    ContentType.entries.forEach { type ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (contentType == type),
                                    onClick = { contentType = type },
                                    role = Role.RadioButton
                                )
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (contentType == type),
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (type) {
                                    ContentType.TODOS -> "Todos"
                                    ContentType.PELICULAS -> "Películas"
                                    ContentType.SERIES -> "Series"
                                },
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = onlyFriends,
                            onClick = { onlyFriends = !onlyFriends },
                            role = Role.Checkbox
                        )
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = onlyFriends,
                        onCheckedChange = { onlyFriends = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Solo películas que gustan a tus amigos",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onApplyFilters(sortOption, selectedGenres, contentType, onlyFriends)
                    onDismiss()
                }
            ) {
                Text("Aplicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
fun GenreChip(
    genre: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(genre) },
        modifier = modifier
    )
}

