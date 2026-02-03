package com.example.cinemiron.data.mapper

import com.example.cinemiron.domain.common.ApiMapper
import com.example.cinemiron.domain.models.MovieDetail
import com.example.cinemiron.data.remote.models.MovieDetailDTO
import com.example.cinemiron.domain.models.Genre as DomainGenre
import com.example.cinemiron.data.remote.models.Genre as RemoteGenre

class MovieDetailApiMapperImpl: ApiMapper<MovieDetail, MovieDetailDTO> {

    override fun mapToDomain(apiDto: MovieDetailDTO): MovieDetail {
        return MovieDetail(
            adult = apiDto.adult,
            backdrop_path = formatEmptyValue(apiDto.backdropPath),
            budget = apiDto.budget,
            genres = mapGenres(apiDto.genres),
            homepage = formatEmptyValue(apiDto.homepage),
            id = apiDto.id,
            imdb_id = formatEmptyValue(apiDto.imdbId),
            original_title = formatEmptyValue(apiDto.originalTitle, "title_original"),
            overview = formatEmptyValue(apiDto.overview, "overview"),
            popularity = apiDto.popularity,
            poster_path = formatEmptyValue(apiDto.posterPath),
            release_date = formatEmptyValue(apiDto.releaseDate, "date"),
            revenue = apiDto.revenue,
            runtime = apiDto.runtime,
            status = formatEmptyValue(apiDto.status),
            title = formatEmptyValue(apiDto.title, "title"),
            video = apiDto.video,
            vote_average = apiDto.voteAverage,
            vote_count = apiDto.voteCount
        )
    }

    private fun formatEmptyValue(value: String?, default: String = ""): String {
        if (value.isNullOrEmpty()) return "Unknown $default"
        return value
    }

    private fun mapGenres(remoteGenres: List<RemoteGenre>): List<DomainGenre> {
        return remoteGenres.map { remoteGenre ->
            DomainGenre(
                id = remoteGenre.id,
                name = formatEmptyValue(remoteGenre.name, "genre")
            )
        }
    }
}

