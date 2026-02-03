package com.example.cinemiron.data.local.models.local.models

data class Resenas (
    val userImage: Int,
    val userName: String,
    val date: String,
    val movieTitle: String,
    val rating: Int,
    val reviewTitle: String,
    val reviewDescription: String,
    val hasSpoiler: Boolean,
    val likes: Int
)