package com.example.cinemiron.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinemiron.core.utils.Response
import com.example.cinemiron.data.local.models.local.models.UserProfile
import com.example.cinemiron.domain.models.MovieDetail
import com.example.cinemiron.domain.repository.FavouriteRepository
import com.example.cinemiron.domain.repository.MovieRepository
import com.example.cinemiron.domain.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class ProfileUiState(
    val userProfile: UserProfile? = null,
    val isLoadingProfile: Boolean = false,
    val isSavingProfile: Boolean = false,
    val favoriteMovies: List<MovieDetail> = emptyList(),
    val isLoadingFavorites: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val favoritesRepository: FavouriteRepository,
    private val movieRepository: MovieRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _favoriteIds = MutableStateFlow<List<Int>>(emptyList())

    fun getCurrentUser() = auth.currentUser

    init {
        loadUserProfile()
        loadFavorites()
    }

    fun loadUserProfile() {
        val userId = auth.currentUser?.uid ?: return
        _uiState.update { it.copy(isLoadingProfile = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val profile = profileRepository.loadUserProfile(userId)
                _uiState.update { it.copy(userProfile = profile, isLoadingProfile = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingProfile = false, errorMessage = e.message) }
            }
        }
    }

    fun updateUserProfile(
        bio: String,
        ubicacion: String,
        fotoUrl: String,
        perfilPublico: Boolean
    ) {
        val userId = auth.currentUser?.uid ?: return
        _uiState.update { it.copy(isSavingProfile = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                profileRepository.updateUserProfileInfo(
                    userId = userId,
                    bio = bio,
                    ubicacion = ubicacion,
                    fotoUrl = fotoUrl,
                    perfilPublico = perfilPublico
                )
                loadUserProfile()
                _uiState.update { it.copy(isSavingProfile = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSavingProfile = false, errorMessage = e.message) }
            }
        }
    }

    fun loadFavorites() {
        val userId = auth.currentUser?.uid ?: return
        _uiState.update { it.copy(isLoadingFavorites = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val ids = favoritesRepository.getFavouriteIds(userId)
                Log.d("Profile", "IDs encontrados: $ids")
                _favoriteIds.value = ids

                val movies = ids.map { id ->
                    async {
                        try {
                            val response = movieRepository.fetchMovieDetail(id)
                                .filterIsInstance<Response.Success<MovieDetail>>()
                                .first()
                            response.data
                        } catch (e: Exception) {
                            Log.e("Profile", "Error cargando película $id", e)
                            null
                        }
                    }
                }.awaitAll().filterNotNull()

                Log.d("Profile", "Películas cargadas: ${movies.size}")
                _uiState.update { it.copy(favoriteMovies = movies, isLoadingFavorites = false) }
            } catch (e: Exception) {
                Log.e("Profile", "Error en loadFavorites", e)
                _uiState.update { it.copy(isLoadingFavorites = false, errorMessage = e.message) }
            }
        }
    }

    fun toggleFavorite(movieId: Int) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                if (favoritesRepository.isFavourite(userId, movieId)) {
                    favoritesRepository.removeFavourite(userId, movieId)
                } else {
                    favoritesRepository.addFavourite(userId, movieId)
                }
                loadFavorites()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }
}