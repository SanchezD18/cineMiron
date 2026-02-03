package com.example.cinemiron.core.di

import com.example.cinemiron.core.utils.K
import com.example.cinemiron.data.mapper.MovieApiMapperImpl
import com.example.cinemiron.data.mapper.MovieDetailApiMapperImpl
import com.example.cinemiron.data.remote.api.MovieApiService
import com.example.cinemiron.data.remote.models.MovieDetailDTO
import com.example.cinemiron.data.remote.models.MovieDto
import com.example.cinemiron.data.repository.MovieRepositoryImpl
import com.example.cinemiron.domain.common.ApiMapper
import com.example.cinemiron.domain.models.Movie
import com.example.cinemiron.domain.models.MovieDetail
import com.example.cinemiron.domain.repository.MovieRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MovieModule {

    private val json = Json {
        coerceInputValues = true
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun provideMovieRepository(
        movieApiService: MovieApiService,
        mapper: ApiMapper<List<Movie>, MovieDto>,
        movieDetailMapper: ApiMapper<MovieDetail, MovieDetailDTO>
    ) : MovieRepository = MovieRepositoryImpl(
        movieApiService, mapper, movieDetailMapper
    )

    @Provides
    @Singleton
    fun providoMovieMapper(): ApiMapper<List<Movie>, MovieDto> = MovieApiMapperImpl()

    @Provides
    @Singleton
    fun provideMovieDetailMapper(): ApiMapper<MovieDetail, MovieDetailDTO> = MovieDetailApiMapperImpl()

    @Provides
    @Singleton
    fun provideMovieApiService(): MovieApiService{
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(K.BASE_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(MovieApiService::class.java)
    }

}