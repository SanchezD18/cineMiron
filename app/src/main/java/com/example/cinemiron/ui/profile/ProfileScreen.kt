package com.example.cinemiron.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cinemiron.data.local.models.local.models.UserBasicInfo
import com.example.cinemiron.data.local.models.local.models.UserProfileInfo
import com.example.cinemiron.ui.components.EditProfileDialog
import com.example.cinemiron.ui.components.FavCard
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ProfileScreen(
    navController: NavController,
    modifier: Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val currentUser = viewModel.getCurrentUser()
    val uiState by viewModel.uiState.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }


    if (currentUser == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Por favor, inicia sesión para ver tu perfil")
        }
        return
    }


    // Recargar perfil si cambia el usuario
    LaunchedEffect(currentUser?.uid) {
        viewModel.loadUserProfile()
        viewModel.loadFavorites()
    }

    Column(
        modifier = Modifier
            .padding(20.dp)
            .verticalScroll(scrollState)
    ) {
        when {
            uiState.isLoadingProfile -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            else -> {
                val profile = uiState.userProfile
                val basicInfo = profile?.basicInfo ?: UserBasicInfo()
                val profileInfo = profile?.profileInfo ?: UserProfileInfo()

                // Cabecera con avatar y botón editar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            val initial = basicInfo.nombre.firstOrNull()?.uppercase() ?: "U"
                            Text(
                                text = initial,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    IconButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Editar perfil",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Info básica
                Column {
                    Text(
                        text = basicInfo.nombre.ifEmpty { "Usuario" },
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = profileInfo.bio.ifEmpty { "No hay biografía aún" },
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (profileInfo.ubicacion.isNotEmpty()) {
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = "📍 ${profileInfo.ubicacion}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.size(20.dp))

                // Estadísticas rápidas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        icon = Icons.Filled.Star,
                        value = "12", // TODO: conectar con repositorio de reseñas
                        label = "Reseñas",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    StatItem(
                        icon = Icons.Filled.Favorite,
                        value = uiState.favoriteMovies.size.toString(),
                        label = "Favoritos",
                        tint = MaterialTheme.colorScheme.error
                    )
                    StatItem(
                        icon = Icons.Filled.AccountCircle,
                        value = formatDate(basicInfo.fechaRegistro),
                        label = "Miembro desde",
                        tint = MaterialTheme.colorScheme.primary,
                        isDate = true
                    )
                }

                // Géneros favoritos (estáticos por ahora)
                Text(
                    text = "Géneros Favoritos:",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Ciencia Ficción", "Thriller", "Drama", "Acción").forEach { genre ->
                        SimpleChip(text = genre)
                    }
                }

                // Películas favoritas
                Text(
                    text = "Películas favoritas:",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                if (uiState.isLoadingFavorites) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(uiState.favoriteMovies) { movie ->
                            FavCard(movie, navController)
                        }
                    }
                }

                Spacer(modifier = Modifier.size(20.dp))

                // Más estadísticas (totales)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "3", // TODO: conectar con reseñas
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Reseñas totales",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "15", // TODO: películas vistas
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Películas vistas",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "4.2", // TODO: nota promedio
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Nota promedio",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }
    }

    // Diálogo de edición
    if (showEditDialog && uiState.userProfile != null) {
        EditProfileDialog(
            onDismiss = { showEditDialog = false },
            onSave = { bio, ubicacion, fotoUrl, perfilPublico ->
                viewModel.updateUserProfile(
                    bio = bio,
                    ubicacion = ubicacion,
                    fotoUrl = fotoUrl,
                    perfilPublico = perfilPublico
                )
                showEditDialog = false
            },
            initialBio = uiState.userProfile?.profileInfo?.bio ?: "",
            initialUbicacion = uiState.userProfile?.profileInfo?.ubicacion ?: "",
            initialFotoUrl = uiState.userProfile?.profileInfo?.fotoUrl ?: "",
            initialPerfilPublico = uiState.userProfile?.profileInfo?.perfilPublico ?: false,
            isLoading = uiState.isSavingProfile
        )
    }
}

@Composable
fun StatItem(
    icon: ImageVector,
    value: String,
    label: String,
    tint: androidx.compose.ui.graphics.Color,
    isDate: Boolean = false
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(16.dp)
            )
            if (isDate) {
                Text(
                    text = value,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            } else {
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = tint
                )
            }
        }
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun SimpleChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 14.sp
        )
    }
}

fun formatDate(timestamp: com.google.firebase.Timestamp?): String {
    if (timestamp == null) return "Fecha no disponible"
    return try {
        val date = timestamp.toDate()
        val format = SimpleDateFormat("MMMM yyyy", Locale("es", "ES"))
        format.format(date)
    } catch (e: Exception) {
        "Fecha no disponible"
    }
}