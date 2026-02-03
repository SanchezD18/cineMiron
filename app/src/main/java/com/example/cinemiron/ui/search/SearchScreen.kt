package com.example.cinemiron.ui.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cinemiron.data.local.models.local.models.UserProfile
import com.example.cinemiron.data.local.repository.loadUserProfile
import com.example.cinemiron.ui.components.MovieCard
import com.example.cinemiron.ui.components.SearchFiltersDialog
import com.example.cinemiron.ui.components.UserSearchCard
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlin.collections.forEach
import kotlin.text.contains
import kotlin.text.isEmpty
import kotlin.text.isNotEmpty
import kotlin.text.lowercase

@Composable
fun SearchScreen(navController: NavController, modifier: Modifier = Modifier) {
    val viewModel = hiltViewModel<SearchViewModel>()
    val searchState by viewModel.searchState.collectAsState()
    val currentQuery by viewModel.currentQuery.collectAsState()

    SearchAPIApp(
        modifier = modifier,
        navController = navController,
        viewModel = viewModel,
        searchState = searchState,
        currentQuery = currentQuery
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAPIApp(
    modifier: Modifier,
    navController: NavController,
    viewModel: SearchViewModel,
    searchState: SearchState,
    currentQuery: String
) {
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    val gridState = rememberLazyStaggeredGridState()

    var users by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var nearbyUsers by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var isLoadingUsers by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()
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
                        loadNearbyUsersAPI(userLocation) { nearbyList ->
                            nearbyUsers = nearbyList
                        }
                    } else {
                        loadPublicUsersAPI { publicList ->
                            nearbyUsers = publicList
                        }
                    }
                },
                onError = {
                    loadPublicUsersAPI { publicList ->
                        nearbyUsers = publicList
                    }
                }
            )
        } else {
            loadPublicUsersAPI { publicList ->
                nearbyUsers = publicList
            }
        }
    }
    LaunchedEffect(currentQuery, selectedTabIndex) {
        if (selectedTabIndex == 1 && currentQuery.isNotEmpty()) {
            isLoadingUsers = true
            searchUsersAPI(currentQuery) { userList ->
                users = userList
                isLoadingUsers = false
            }
        } else if (selectedTabIndex == 1 && currentQuery.isEmpty()) {
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
            DockedSearchBarAPI(
                currentQuery = currentQuery,
                onQueryChange = { viewModel.onSearchQueryChanged(it) },
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
                text = { Text("Películas API") }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Usuarios") }
            )
        }

        when (selectedTabIndex) {
            0 -> {
                if (searchState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (searchState.error != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Error: ${searchState.error}")
                    }
                } else {
                    LazyVerticalStaggeredGrid(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        columns = StaggeredGridCells.Fixed(1),
                        state = gridState,
                    ) {
                        items(searchState.searchMovies) { movie ->
                            MovieCard(
                                movie = movie,
                                onClick = {
                                    navController.navigate("filminfo/${movie.id}")
                                }
                            )
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
                    if (currentQuery.isEmpty()) {
                        item {
                            Text(
                                text = "Usuarios cercanos",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        items(nearbyUsers) { user ->
                            UserSearchCard(
                                user = user,
                                onClick = { /* Navegar a perfil de usuario */ }
                            )
                        }
                    } else {
                        if (isLoadingUsers) {
                            item {
                                Text(
                                    text = "Buscando usuarios...",
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        } else {
                            items(users) { user ->
                                UserSearchCard(
                                    user = user,
                                    onClick = { /* Navegar a perfil de usuario */ }
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
                onApplyFilters = {  sortOption, selectedGenres, contentType, onlyFriends ->
                    TODO()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DockedSearchBarAPI(
    currentQuery: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    DockedSearchBar(
        modifier = modifier.padding(10.dp),
        inputField = {
            SearchBarDefaults.InputField(
                query = currentQuery,
                onQueryChange = onQueryChange,
                onSearch = { expanded = false },
                expanded = expanded,
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

fun searchUsersAPI(
    query: String,
    onResult: (List<UserProfile>) -> Unit
) {
    if (query.isEmpty()) {
        onResult(emptyList())
        return
    }

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
            onResult(userList)
        }
        .addOnFailureListener { e ->
            onResult(emptyList())
        }
}

fun loadNearbyUsersAPI(
    location: String,
    onResult: (List<UserProfile>) -> Unit
) {

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
            onResult(userList)
        }
        .addOnFailureListener { e ->
            loadPublicUsersAPI(onResult)
        }
}

fun loadPublicUsersAPI(
    onResult: (List<UserProfile>) -> Unit
) {

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
            onResult(userList)
        }
        .addOnFailureListener { e ->
            onResult(emptyList())
        }
}