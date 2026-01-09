package com.example.cinemiron.tmp_movie.data.remote.api

import com.example.cinemiron.BuildConfig
import com.example.cinemiron.tmp_movie.data.remote.models.MovieDto
import com.example.cinemiron.tmp_utils.K
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApiService {

    @GET(K.MOVIE_ENDPOINT)
    suspend fun fetchDiscoverMovie(
        @Query("api_key") apiKey: String = BuildConfig.apiKey,
        @Query("include_adult") includeAdult: Boolean = false
    ) : MovieDto

    @GET(K.TRENDING_MOVIE_ENDPOINT)
    suspend fun fetchTrendingMovie(
        @Query("api_key") apiKey: String = BuildConfig.apiKey,
        @Query("include_adult") includeAdult: Boolean = false
    ) : MovieDto
}