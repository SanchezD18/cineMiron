package com.example.cinemiron.tmp_ui.filminfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinemiron.tmp_movie.domain.models.MovieDetail
import com.example.cinemiron.tmp_movie.domain.repository.MovieRepository
import com.example.cinemiron.tmp_utils.collectAndHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilmInfoViewModel @Inject constructor(
    private val repository: MovieRepository,
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
        }
    }
}

data class FilmInfoState(
    val movieDetail: MovieDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

