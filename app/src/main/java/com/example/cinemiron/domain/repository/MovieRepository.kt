package com.example.cinemiron.domain.repository

import com.example.cinemiron.domain.models.Movie
import com.example.cinemiron.domain.models.MovieDetail
import com.example.cinemiron.core.utils.Response
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun fetchDiscoverMovie(): Flow<Response<List<Movie>>>
    fun fetchTrendingMovie(): Flow<Response<List<Movie>>>
    fun fetchUpcomingMovie(): Flow<Response<List<Movie>>>
    fun fetchMovieDetail(movieId: Int): Flow<Response<MovieDetail>>
    fun fetchMovieTrailer(movieId: Int): Flow<Response<String>>

    fun fetchSearchMovie(querytext: String): Flow<Response<List<Movie>>>
}