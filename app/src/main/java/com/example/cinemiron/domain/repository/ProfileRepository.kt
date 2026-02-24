package com.example.cinemiron.domain.repository

import com.example.cinemiron.data.local.models.local.models.UserProfile

interface ProfileRepository {
    suspend fun loadUserProfile(userId: String): UserProfile?
    suspend fun updateUserProfileInfo(
        userId: String,
        bio: String,
        ubicacion: String,
        fotoUrl: String,
        perfilPublico: Boolean
    )
    suspend fun saveUserProfileToFirestore(
        userId: String,
        email: String,
        username: String
    )
}