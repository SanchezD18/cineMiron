package com.example.cinemiron.domain.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


@Singleton
class FavoritesRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FavouriteRepository {

    private fun getUserFavoritesCollection(userId: String) =
        firestore.collection("users").document(userId).collection("favorites")

    override suspend fun addFavourite(userId: String, movieId: Int) {
        return suspendCoroutine { continuation ->
            val data = mapOf("addedAt" to FieldValue.serverTimestamp())
            getUserFavoritesCollection(userId)
                .document(movieId.toString())
                .set(data)
                .addOnSuccessListener { continuation.resume(Unit) }
                .addOnFailureListener { continuation.resumeWithException(it) }
        }
    }

    override suspend fun removeFavourite(userId: String, movieId: Int) {
        return suspendCoroutine { continuation ->
            getUserFavoritesCollection(userId)
                .document(movieId.toString())
                .delete()
                .addOnSuccessListener { continuation.resume(Unit) }
                .addOnFailureListener { continuation.resumeWithException(it) }
        }
    }

    override suspend fun isFavourite(userId: String, movieId: Int): Boolean {
        return suspendCoroutine { continuation ->
            getUserFavoritesCollection(userId)
                .document(movieId.toString())
                .get()
                .addOnSuccessListener { document ->
                    continuation.resume(document.exists())
                }
                .addOnFailureListener { continuation.resumeWithException(it) }
        }
    }

    override suspend fun getFavouriteIds(userId: String): List<Int> {
        return suspendCoroutine { continuation ->
            getUserFavoritesCollection(userId)
                .get()
                .addOnSuccessListener { snapshot ->
                    val ids = snapshot.documents.mapNotNull { doc ->
                        doc.id.toIntOrNull()
                    }
                    continuation.resume(ids)
                }
                .addOnFailureListener { continuation.resumeWithException(it) }
        }
    }
}