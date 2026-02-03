package com.example.cinemiron.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cinemiron.R
import com.example.cinemiron.data.local.models.local.models.UserBasicInfo
import com.example.cinemiron.data.local.models.local.models.UserProfile
import com.example.cinemiron.data.local.models.local.models.UserProfileInfo
import com.example.cinemiron.data.local.repository.loadUserProfile
import com.example.cinemiron.data.local.repository.updateUserProfileInfo
import com.example.cinemiron.ui.components.EditProfileDialog
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun ProfileScreen(navController: NavController, modifier: Modifier, auth: FirebaseAuth) {
    val scrollState = rememberScrollState()
    val currentUser = auth.currentUser
    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showEditDialog by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(currentUser?.uid) {
        val userId = currentUser?.uid
        if (userId != null) {
            isLoading = true
            loadUserProfile(
                userId = userId,
                onSuccess = { profile ->
                    userProfile = profile
                    isLoading = false
                },
                onError = { exception ->
                    isLoading = false
                }
            )
        } else {
            isLoading = false
        }
    }

    Column(
        Modifier.padding(20.dp)
            .verticalScroll(scrollState)
    ) {

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val profile = userProfile
            val basicInfo = profile?.basicInfo ?: UserBasicInfo()
            val profileInfo = profile?.profileInfo ?: UserProfileInfo()

            Row(
                Modifier.fillMaxWidth(),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ) {
                Surface(
                    modifier.size(100.dp),
                    CircleShape,
                    MaterialTheme.colorScheme.primary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        val initial = basicInfo.nombre.firstOrNull()?.uppercase() ?: "U"
                        Text(text = initial, fontSize = 30.sp, fontWeight = FontWeight.Bold)
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
            Column {
                Text(
                    text = basicInfo.nombre.ifEmpty { "Usuario" },
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.size(6.dp))
                Text(
                    text = profileInfo.bio.ifEmpty { "No hay biografía aún" },
                    style = MaterialTheme.typography.bodyMedium
                )
                if (profileInfo.ubicacion.isNotEmpty()) {
                    Spacer(Modifier.size(4.dp))
                    Text(
                        text = "📍 ${profileInfo.ubicacion}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.size(20.dp))
            Row(
                Modifier.fillMaxWidth(),
                Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Reseñas",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "12",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = "Reseñas",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Reseñas",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "8",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Text(
                        text = "Favoritos",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Miembro desde",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "CirculoCuenta",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = formatDate(basicInfo.fechaRegistro),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            val favoriteGenres = listOf("Ciencia Ficción", "Thriller", "Drama", "Acción")
            Text(
                text = "Géneros Favoritos:",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Row(
                Modifier.padding(12.dp),
                Arrangement.SpaceEvenly
            ) {
                for (genre in favoriteGenres) {
                    SimpleChip(genre)
                }
            }
            val imagenes = listOf(
                R.drawable.titanic,
                R.drawable.titanic,
                R.drawable.titanic,
                R.drawable.titanic,
                R.drawable.titanic,
                R.drawable.titanic,
                R.drawable.titanic,
                R.drawable.titanic,
                R.drawable.titanic,
                R.drawable.titanic,
                R.drawable.titanic,
                R.drawable.titanic,
                R.drawable.titanic,
                R.drawable.titanic
            )
            Text(
                text = "Películas favoritas:",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(imagenes) { imageRes ->
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = "Imagen",
                        modifier = Modifier
                            .width(100.dp)
                            .height(150.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(Modifier.size(20.dp))
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Películas favoritas:",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "3",
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
                            text = "15",
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
                            text = "4.2",
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

            if (showEditDialog && userProfile != null) {
                EditProfileDialog(
                    onDismiss = { showEditDialog = false },
                    onSave = { bio, ubicacion, fotoUrl, perfilPublico ->
                        val userId = currentUser?.uid
                        if (userId != null) {
                            isSaving = true
                            updateUserProfileInfo(
                                userId = userId,
                                bio = bio,
                                ubicacion = ubicacion,
                                fotoUrl = fotoUrl,
                                perfilPublico = perfilPublico,
                                onSuccess = {
                                    isSaving = false
                                    showEditDialog = false
                                    coroutineScope.launch {
                                        loadUserProfile(
                                            userId = userId,
                                            onSuccess = { profile ->
                                                userProfile = profile
                                            },
                                            onError = { exception ->
                                            }
                                        )
                                    }
                                },
                                onError = { exception ->
                                    isSaving = false
                                }
                            )
                        }
                    },
                    initialBio = userProfile?.profileInfo?.bio ?: "",
                    initialUbicacion = userProfile?.profileInfo?.ubicacion ?: "",
                    initialFotoUrl = userProfile?.profileInfo?.fotoUrl ?: "",
                    initialPerfilPublico = userProfile?.profileInfo?.perfilPublico ?: false,
                    isLoading = isSaving
                )
            }
        }
    }
}

fun formatDate(timestamp: com.google.firebase.Timestamp?): String {
    if (timestamp == null) {
        return "Fecha no disponible"
    }
    return try {
        val date = timestamp.toDate()
        val format = SimpleDateFormat("MMMM yyyy", Locale("es", "ES"))
        val formatted = format.format(date)
        formatted
    } catch (e: Exception) {
        "Fecha no disponible"
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