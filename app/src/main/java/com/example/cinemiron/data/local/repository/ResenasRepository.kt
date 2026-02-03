package com.example.cinemiron.data.local.repository

import com.example.cinemiron.R
import com.example.cinemiron.data.local.models.local.models.Resenas

object ResenasRepository {
    fun fakeReviews(): List<Resenas> = listOf(
        Resenas(
            userImage = R.drawable.ic_launcher_foreground,
            userName = "Carlos",
            date = "10/12/2025",
            movieTitle = "Inception",
            rating = 9,
            reviewTitle = "Una obra maestra",
            reviewDescription = "Una película compleja pero brillante",
            hasSpoiler = true,
            likes = 12
        )
    )
}