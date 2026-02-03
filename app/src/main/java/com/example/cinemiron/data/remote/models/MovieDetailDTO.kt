package com.example.cinemiron.data.remote.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDetailDTO(
    @SerialName("adult")
    val adult: Boolean,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("budget")
    val budget: Int,
    @SerialName("genres")
    val genres: List<Genre>,
    @SerialName("homepage")
    val homepage: String? = null,
    @SerialName("id")
    val id: Int,
    @SerialName("imdb_id")
    val imdbId: String? = null,
    @SerialName("origin_country")
    val originCountry: List<String>,
    @SerialName("original_language")
    val originalLanguage: String,
    @SerialName("original_title")
    val originalTitle: String,
    @SerialName("overview")
    val overview: String? = null,
    @SerialName("popularity")
    val popularity: Double,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("production_companies")
    val productionCompanies: List<ProductionCompany>,
    @SerialName("production_countries")
    val productionCountries: List<ProductionCountry>,
    @SerialName("release_date")
    val releaseDate: String? = null,
    @SerialName("revenue")
    val revenue: Int,
    @SerialName("runtime")
    val runtime: Int,
    @SerialName("spoken_languages")
    val spokenLanguages: List<SpokenLanguage>,
    @SerialName("status")
    val status: String,
    @SerialName("tagline")
    val tagline: String? = null,
    @SerialName("title")
    val title: String,
    @SerialName("video")
    val video: Boolean,
    @SerialName("vote_average")
    val voteAverage: Double,
    @SerialName("vote_count")
    val voteCount: Int
)