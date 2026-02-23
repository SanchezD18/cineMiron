package com.example.cinemiron.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinemiron.domain.models.Movie
import com.example.cinemiron.domain.repository.FavoritesRepository
import com.example.cinemiron.domain.repository.FavouriteRepository
import com.example.cinemiron.domain.repository.MovieRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Si usas Hilt, anota; si no, puedes instanciar manualmente
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val favoritesRepository: FavouriteRepository,
    private val movieRepository: MovieRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _favouriteIds = MutableStateFlow<List<Int>>(emptyList())
    val favouriteIds: StateFlow<List<Int>> = _favouriteIds

    private val _isLoadingFavorites = MutableStateFlow(false)
    val isLoadingFavorites: StateFlow<Boolean> = _isLoadingFavorites

    fun loadFavorites() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoadingFavorites.value = true
            try {
                val ids = favoritesRepository.getFavoriteIds(userId)
                // Obtener detalles de cada película en paralelo
                val movies = ids.map { id ->
                    async { movieRepository.getMovieDetails(id) }
                }.awaitAll()
                _favoriteMovies.value = movies
            } catch (e: Exception) {
                // Manejar error
            } finally {
                _isLoadingFavorites.value = false
            }
        }
    }
}