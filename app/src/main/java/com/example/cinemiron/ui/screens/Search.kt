package com.example.cinemiron.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cinemiron.data.PelisRepository
import com.example.cinemiron.data.UserProfile
import com.example.cinemiron.data.loadUserProfile
import com.example.cinemiron.ui.components.CardPelis
import com.example.cinemiron.ui.components.SearchFiltersDialog
import com.example.cinemiron.ui.components.SortOption
import com.example.cinemiron.ui.components.ContentType
import com.example.cinemiron.ui.components.UserSearchCard
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

private const val TAG = "SearchScreen"

@Composable
fun SearchScreen(navController: NavController, modifier: Modifier = Modifier) {
    val auth = Firebase.auth
    SearchApp(modifier, navController, auth)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchApp(modifier: Modifier, navController: NavController, auth: FirebaseAuth) {
    val pelis = remember { PelisRepository.getContactInfo() }
    val textSearch = rememberTextFieldState()
    var textOnSearch by rememberSaveable { mutableStateOf("") }
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    val gridState = rememberLazyStaggeredGridState()

    var users by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var nearbyUsers by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var isLoadingUsers by remember { mutableStateOf(false) }
    val currentUser = auth.currentUser

    var showFiltersDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(currentUser?.uid) {
        val userId = currentUser?.uid
        if (userId != null) {
            loadUserProfile(
                userId = userId,
                onSuccess = { currentProfile ->
                    val userLocation = currentProfile.profileInfo.ubicacion
                    if (userLocation.isNotEmpty()) {
                        loadNearbyUsers(userLocation) { nearbyList ->
                            nearbyUsers = nearbyList
                        }
                    } else {
                        loadPublicUsers { publicList ->
                            nearbyUsers = publicList
                        }
                    }
                },
                onError = {
                    loadPublicUsers { publicList ->
                        nearbyUsers = publicList
                    }
                }
            )
        } else {
            loadPublicUsers { publicList ->
                nearbyUsers = publicList
            }
        }
    }

    LaunchedEffect(textOnSearch) {
        if (selectedTabIndex == 1 && textOnSearch.isNotEmpty()) {
            isLoadingUsers = true
            searchUsers(textOnSearch) { userList ->
                users = userList
                isLoadingUsers = false
            }
        } else if (selectedTabIndex == 1 && textOnSearch.isEmpty()) {
            users = emptyList()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DockedSearchBarConFiltro(
                textFieldState = textSearch,
                onSearch = { textOnSearch = it },
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { showFiltersDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Filtros",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        TabRow(selectedTabIndex = selectedTabIndex) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("Películas") }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Usuarios") }
            )
        }

        when (selectedTabIndex) {
            0 -> {
                LazyVerticalStaggeredGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    columns = StaggeredGridCells.Fixed(1),
                    state = gridState,
                ) {
                    items(pelis) { peli ->
                        val comentario = peli.name
                        if (textOnSearch.isEmpty() || comentario.contains(
                                textOnSearch,
                                ignoreCase = true
                            )
                        ) {
                            CardPelis(peli, onClick = { navController.navigate("filminfo") })
                        }
                    }
                }
            }
            1 -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    if (textOnSearch.isEmpty()) {
                        item {
                            Text(
                                text = "Busqueda",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        items(nearbyUsers) { user ->
                            UserSearchCard(
                                user = user,
                                onClick = { /* TODO: Navegar a perfil de usuario */ }
                            )
                        }
                    } else {
                        if (isLoadingUsers) {
                            item {
                                Text(
                                    text = "Buscando...",
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        } else {
                            items(users) { user ->
                                UserSearchCard(
                                    user = user,
                                    onClick = { /* TODO: Navegar a perfil de usuario */ }
                                )
                            }
                            if (users.isEmpty()) {
                                item {
                                    Text(
                                        text = "No se encontraron usuarios",
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showFiltersDialog) {
            SearchFiltersDialog(
                onDismiss = { showFiltersDialog = false },
                onApplyFilters = { sortOption, selectedGenres, contentType, onlyFriends ->
                    // TODO: Aplicar filtros cuando se implemente la funcionalidad
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DockedSearchBarConFiltro(
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(textFieldState.text.toString()) {
        onSearch(textFieldState.text.toString())
    }

    DockedSearchBar(
        modifier = modifier
            .padding(10.dp),
        inputField = {
            SearchBarDefaults.InputField(
                query = textFieldState.text.toString(),
                onQueryChange = { textFieldState.edit { replace(0, length, it) } },
                onSearch = {
                    onSearch(textFieldState.text.toString())
                    expanded = false
                },
                expanded = false,
                onExpandedChange = { expanded = false },
                placeholder = { Text("Buscar", color = Color.Gray) },
                colors = SearchBarDefaults.inputFieldColors(
                    focusedTextColor = Color.Red
                )
            )
        },
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {

    }
}

fun searchUsers(
    query: String,
    onResult: (List<UserProfile>) -> Unit
) {
    if (query.isEmpty()) {
        onResult(emptyList())
        return
    }

    Log.d(TAG, "Buscando usuarios con query: $query")

    Firebase.firestore.collection("users")
        .whereEqualTo("profileInfo.perfilPublico", true)
        .limit(50)
        .get()
        .addOnSuccessListener { querySnapshot ->
            val userList = mutableListOf<UserProfile>()
            val queryLower = query.lowercase()
            
            querySnapshot.documents.forEach { document ->
                val profile = UserProfile.fromDocument(document)
                if (profile != null) {
                    val nombre = profile.basicInfo.nombre.lowercase()
                    val bio = profile.profileInfo.bio.lowercase()
                    val ubicacion = profile.profileInfo.ubicacion.lowercase()

                    if (nombre.contains(queryLower) || 
                        bio.contains(queryLower) || 
                        ubicacion.contains(queryLower)) {
                        userList.add(profile)
                    }
                }
            }
            Log.d(TAG, "Usuarios encontrados: ${userList.size}")
            onResult(userList)
        }
        .addOnFailureListener { e ->
            Log.e(TAG, "Error buscando usuarios: ${e.message}", e)
            onResult(emptyList())
        }
}

fun loadNearbyUsers(
    location: String,
    onResult: (List<UserProfile>) -> Unit
) {
    Log.d(TAG, "Cargando usuarios cercanos de: $location")

    Firebase.firestore.collection("users")
        .whereEqualTo("profileInfo.ubicacion", location)
        .whereEqualTo("profileInfo.perfilPublico", true)
        .limit(10)
        .get()
        .addOnSuccessListener { querySnapshot ->
            val userList = mutableListOf<UserProfile>()
            querySnapshot.documents.forEach { document ->
                val profile = UserProfile.fromDocument(document)
                if (profile != null) {
                    userList.add(profile)
                }
            }
            Log.d(TAG, "Usuarios cercanos encontrados: ${userList.size}")
            onResult(userList)
        }
        .addOnFailureListener { e ->
            Log.e(TAG, "Error cargando usuarios cercanos: ${e.message}", e)
            loadPublicUsers(onResult)
        }
}

fun loadPublicUsers(
    onResult: (List<UserProfile>) -> Unit
) {
    Log.d(TAG, "Cargando usuarios públicos")

    Firebase.firestore.collection("users")
        .whereEqualTo("profileInfo.perfilPublico", true)
        .limit(10)
        .get()
        .addOnSuccessListener { querySnapshot ->
            val userList = mutableListOf<UserProfile>()
            querySnapshot.documents.forEach { document ->
                val profile = UserProfile.fromDocument(document)
                if (profile != null) {
                    userList.add(profile)
                }
            }
            Log.d(TAG, "Usuarios públicos encontrados: ${userList.size}")
            onResult(userList)
        }
        .addOnFailureListener { e ->
            Log.e(TAG, "Error cargando usuarios públicos: ${e.message}", e)
            onResult(emptyList())
        }
}
