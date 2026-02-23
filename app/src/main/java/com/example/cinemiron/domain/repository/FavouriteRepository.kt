package com.example.cinemiron.domain.repository

import com.example.cinemiron.domain.models.Movie

interface FavouriteRepository {
    suspend fun addFavourite(userId: String, movieId: Int)
    suspend fun removeFavourite(userId: String, movieId: Int)
    suspend fun isFavourite(userId: String, movieId: Int) : Boolean
    suspend fun getFavouriteIds(userId: String): List <Int>

}