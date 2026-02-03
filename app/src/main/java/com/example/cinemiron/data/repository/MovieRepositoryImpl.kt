package com.example.cinemiron.data.repository

import com.example.cinemiron.data.remote.api.MovieApiService
import com.example.cinemiron.domain.common.ApiMapper
import com.example.cinemiron.data.remote.models.MovieDto
import com.example.cinemiron.data.remote.models.MovieDetailDTO
import com.example.cinemiron.domain.models.Movie
import com.example.cinemiron.domain.models.MovieDetail
import com.example.cinemiron.domain.repository.MovieRepository
import com.example.cinemiron.core.utils.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class MovieRepositoryImpl(
    private val movieApiService: MovieApiService,
    private val apiMapper: ApiMapper<List<Movie>, MovieDto>,
    private val movieDetailMapper: ApiMapper<MovieDetail, MovieDetailDTO>
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

    override fun fetchUpcomingMovie(): Flow<Response<List<Movie>>> = flow {
        emit(Response.Loading())
        val movieDto = movieApiService.fetchUpcomingMovie()
        apiMapper.mapToDomain(movieDto).apply {
            emit(Response.Success(this))
        }
    }.catch { e ->
        emit(Response.Error(e))
    }

    override fun fetchMovieDetail(movieId: Int): Flow<Response<MovieDetail>> = flow {
        emit(Response.Loading())
        val movieDetailDto = movieApiService.fetchMovieDetail(movieId)
        val movieDetail = movieDetailMapper.mapToDomain(movieDetailDto)
        emit(Response.Success(movieDetail))
    }.catch { e ->
        emit(Response.Error(e))
    }

    override fun fetchMovieTrailer(movieId: Int): Flow<Response<String>> = flow {
        emit(Response.Loading())

        val response = movieApiService.fetchMovieVideos(movieId)

        val trailerKey = response.results
            .firstOrNull { it.site == "YouTube" && it.type == "Trailer" }
            ?.key
            ?: throw Exception("Trailer no encontrado")

        emit(Response.Success(trailerKey))

    }.catch { e ->
        emit(Response.Error(e))
    }

    override fun fetchSearchMovie(querytext: String): Flow<Response<List<Movie>>> = flow {
        emit(Response.Loading())
        val movieDto = movieApiService.fetchSearchMovie(querytext)
        apiMapper.mapToDomain(movieDto).apply {
            emit(Response.Success(this))}
    }.catch { e ->
        emit(Response.Error(e))
    }
}