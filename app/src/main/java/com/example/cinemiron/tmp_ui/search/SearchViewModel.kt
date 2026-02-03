package com.example.cinemiron.tmp_ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinemiron.tmp_movie.domain.models.Movie
import com.example.cinemiron.tmp_movie.domain.repository.MovieRepository
import com.example.cinemiron.tmp_utils.collectAndHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.isNotEmpty

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MovieRepository,
): ViewModel() {
    private val _searchState = MutableStateFlow(SearchState())
    val searchState = _searchState.asStateFlow()



    private val _currentQuery = MutableStateFlow("")
    val currentQuery = _currentQuery.asStateFlow()

    private var searchJob: Job? = null

    fun onSearchQueryChanged(query: String) {


        _currentQuery.value = query

        if (query.isNotEmpty()){
            searchJob = viewModelScope.launch {
                delay(300)
                fetchSearchMovie(query)
            }
        } else {
            clearResults()
        }
    }


    private fun fetchSearchMovie(query : String) = viewModelScope.launch {
        repository.fetchSearchMovie(query).collectAndHandle(
            onError = { error ->
                _searchState.update {
                    it.copy(isLoading = false, error = error?.message)
                }
            },
            onLoading = {
                _searchState.update {
                    it.copy(isLoading = true, error = null)
                }
            }
        ) { movie ->
            _searchState.update {
                it.copy(isLoading = false, error = null, searchMovies = movie)
            }
        }
    }

    fun clearResults() {
        _searchState.update {
            it.copy(searchMovies = emptyList(), isLoading = false)
        }
    }

    fun clearError() {
        _searchState.update { it.copy(error = null) }
    }


}

data class SearchState(
    val searchMovies: List<Movie> = emptyList(),
    val error: String? = null,
    val isLoading: Boolean = false
)