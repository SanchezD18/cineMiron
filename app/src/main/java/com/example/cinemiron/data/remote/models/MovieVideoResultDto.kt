package com.example.cinemiron.data.remote.models

@kotlinx.serialization.Serializable
data class MovieVideoResultDto(
    val id: String,
    val key: String,
    val name: String,
    val site: String,
    val type: String
)

