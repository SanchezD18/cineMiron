package com.example.cinemiron.tmp_movie.domain.repository

import com.example.cinemiron.tmp_movie.domain.models.Movie
import com.example.cinemiron.tmp_movie.domain.models.MovieDetail
import com.example.cinemiron.tmp_utils.Response
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun fetchDiscoverMovie(): Flow<Response<List<Movie>>>
    fun fetchTrendingMovie(): Flow<Response<List<Movie>>>
    fun fetchUpcomingMovie(): Flow<Response<List<Movie>>>
    fun fetchMovieDetail(movieId: Int): Flow<Response<MovieDetail>>
}