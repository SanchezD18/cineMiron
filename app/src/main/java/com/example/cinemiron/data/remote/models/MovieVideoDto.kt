package com.example.cinemiron.data.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class VideoResponseDto(
    val results: List<VideoDto>
)

@Serializable
data class VideoDto(
    val key: String,
    val site: String,
    val type: String
)
