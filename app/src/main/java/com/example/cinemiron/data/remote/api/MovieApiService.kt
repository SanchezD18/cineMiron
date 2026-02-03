package com.example.cinemiron.data.remote.api

import com.example.cinemiron.BuildConfig
import com.example.cinemiron.data.remote.models.MovieDetailDTO
import com.example.cinemiron.data.remote.models.MovieDto
import com.example.cinemiron.data.remote.models.MovieVideoResponseDto
import com.example.cinemiron.core.utils.K
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {

    @GET(K.MOVIE_ENDPOINT)
    suspend fun fetchDiscoverMovie(
        @Query("api_key") apiKey: String = BuildConfig.apikey,
        @Query("include_adult") includeAdult: Boolean = false
    ) : MovieDto

    @GET(K.TRENDING_MOVIE_ENDPOINT)
    suspend fun fetchTrendingMovie(
        @Query("api_key") apiKey: String = BuildConfig.apikey,
        @Query("include_adult") includeAdult: Boolean = false
    ) : MovieDto

    @GET(K.UPCOMING_MOVIE_ENDPOINT)
    suspend fun fetchUpcomingMovie(
        @Query("api_key") apiKey: String = BuildConfig.apikey,
        @Query("include_adult") includeAdult: Boolean = false
    ) : MovieDto

    @GET("${K.MOVIE_DETAIL_ENDPOINT}{movieId}?${K.LANGUAGE}")
    suspend fun fetchMovieDetail(
        @Path("movieId") movieId: Int,
        @Query("api_key") apiKey: String = BuildConfig.apikey
    ) : MovieDetailDTO

    @GET("movie/{movie_id}/videos")
    suspend fun fetchMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = BuildConfig.apikey
    ): MovieVideoResponseDto

    @GET(K.SEARCH_MOVIE_ENPOINT)
    suspend fun fetchSearchMovie(
    @Query("query") movieId: String,
    @Query("api_key") apiKey: String = BuildConfig.apikey,
    @Query("include_adult") includeAdult: Boolean = false
    ): MovieDto
}