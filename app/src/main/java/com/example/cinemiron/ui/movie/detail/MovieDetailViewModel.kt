package com.example.cinemiron.ui.movie.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinemiron.core.utils.collectAndHandle
import com.example.cinemiron.domain.models.MovieDetail
import com.example.cinemiron.domain.repository.MovieRepository
import com.example.cinemiron.core.utils.collectAndHandle
import com.example.cinemiron.domain.repository.FavouriteRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val repository: MovieRepository,
    private val favouriteRepository: FavouriteRepository,
    private val auth: FirebaseAuth
): ViewModel() {
    private val _filmInfoState = MutableStateFlow(FilmInfoState())
    val filmInfoState = _filmInfoState.asStateFlow()

    fun fetchMovieDetail(movieId: Int) = viewModelScope.launch {
        repository.fetchMovieDetail(movieId).collectAndHandle(
            onError = { error ->
                _filmInfoState.update {
                    it.copy(isLoading = false, error = error?.message)
                }
            },
            onLoading = {
                _filmInfoState.update {
                    it.copy(isLoading = true, error = null)
                }
            }
        ) { movieDetail ->
            _filmInfoState.update {
                it.copy(isLoading = false, error = null, movieDetail = movieDetail)
            }

            checkIfFavourite(movieId)

        }
    }

    private fun checkIfFavourite(movieId: Int) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val isFav = favouriteRepository.isFavourite(userId, movieId)
                _filmInfoState.update { it.copy(isFavourite = isFav) }
            } catch (e: Exception) {
                // Ignorar error, no crítico
            }
        }
    }

    fun toggleFavorite() {
        val userId = auth.currentUser?.uid ?: return
        val movieId = _filmInfoState.value.movieDetail?.id ?: return
        viewModelScope.launch {
            try {
                if (_filmInfoState.value.isFavourite) {
                    favouriteRepository.removeFavourite(userId, movieId)
                } else {
                    favouriteRepository.addFavourite(userId, movieId)
                }
                _filmInfoState.update { it.copy(isFavourite = !it.isFavourite) }
            } catch (e: Exception) {
                // Opcional: mostrar error
                e.printStackTrace()
            }
        }
    }


    fun fetchTrailer(movieId: Int, onResult: (String) -> Unit) {
        viewModelScope.launch {
            repository.fetchMovieTrailer(movieId).collectAndHandle(
                onError = { },
                onLoading = { }
            ) { trailerKey ->
                onResult(trailerKey)
            }
        }
    }

}

data class FilmInfoActions(
    val onToggleFavorite: () -> Unit,
    val onPlayTrailer: (Int) -> Unit
)

data class FilmInfoState(
    val movieDetail: MovieDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFavourite: Boolean = false
)

