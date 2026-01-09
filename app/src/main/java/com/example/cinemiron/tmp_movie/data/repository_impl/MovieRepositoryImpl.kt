package com.example.cinemiron.tmp_movie.data.repository_impl

import com.example.cinemiron.tmp_movie.data.remote.api.MovieApiService
import com.example.cinemiron.tmp_common.data.ApiMapper
import com.example.cinemiron.tmp_movie.data.remote.models.MovieDto
import com.example.cinemiron.tmp_movie.domain.models.Movie
import com.example.cinemiron.tmp_movie.domain.repository.MovieRepository
import com.example.cinemiron.tmp_utils.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class MovieRepositoryImpl(
    private val movieApiService: MovieApiService,
    private val apiMapper: ApiMapper<List<Movie>, MovieDto>
): MovieRepository {
    override fun fetchDiscoverMovie(): Flow<Response<List<Movie>>> = flow {
        emit(Response.Loading())
        val movieDto = movieApiService.fetchDiscoverMovie()
        apiMapper.mapToDomain(movieDto).apply {
            emit(Response.Success(this))
        }
    }.catch { e ->
        emit(Response.Error(e))
    }

    override fun fetchTrendingMovie(): Flow<Response<List<Movie>>> = flow {
        emit(Response.Loading())
        val movieDto = movieApiService.fetchTrendingMovie()
        apiMapper.mapToDomain(movieDto).apply {
            emit(Response.Success(this))
        }
    }.catch { e ->
        emit(Response.Error(e))
    }

}