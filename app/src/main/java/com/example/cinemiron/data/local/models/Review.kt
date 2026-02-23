package com.example.cinemiron.data.local.models.local.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ServerTimestamp

data class Review(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val movieId: Int = 0,
    val movieTitle: String = "",
    val moviePosterPath: String = "",
    val rating: Int = 0,
    val reviewTitle: String = "",
    val reviewDescription: String = "",
    val hasSpoiler: Boolean = false,
    val likes: Int = 0,
    @ServerTimestamp
    val createdAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "userId" to userId,
        "userName" to userName,
        "movieId" to movieId,
        "movieTitle" to movieTitle,
        "moviePosterPath" to moviePosterPath,
        "rating" to rating,
        "reviewTitle" to reviewTitle,
        "reviewDescription" to reviewDescription,
        "hasSpoiler" to hasSpoiler,
        "likes" to likes,
        "createdAt" to createdAt
    )

    companion object {
        fun fromDocument(document: DocumentSnapshot): Review? {
            val data = document.data ?: return null
            return Review(
                id = document.id,
                userId = data["userId"] as? String ?: "",
                userName = data["userName"] as? String ?: "",
                movieId = (data["movieId"] as? Number)?.toInt() ?: 0,
                movieTitle = data["movieTitle"] as? String ?: "",
                moviePosterPath = data["moviePosterPath"] as? String ?: "",
                rating = (data["rating"] as? Number)?.toInt() ?: 0,
                reviewTitle = data["reviewTitle"] as? String ?: "",
                reviewDescription = data["reviewDescription"] as? String ?: "",
                hasSpoiler = data["hasSpoiler"] as? Boolean ?: false,
                likes = (data["likes"] as? Number)?.toInt() ?: 0,
                createdAt = data["createdAt"] as? Timestamp
            )
        }
    }
}
