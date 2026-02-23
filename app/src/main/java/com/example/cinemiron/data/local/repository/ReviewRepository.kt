package com.example.cinemiron.data.local.repository

import android.util.Log
import com.example.cinemiron.data.local.models.local.models.Review
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

private const val TAG = "ReviewRepository"
private const val COLLECTION = "reviews"

object ReviewRepository {

    fun addReview(
        review: Review,
        onSuccess: (Review) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val reviewData = review.toMap().toMutableMap()
        reviewData["createdAt"] = Timestamp.now()

        Firebase.firestore.collection(COLLECTION)
            .add(reviewData)
            .addOnSuccessListener { docRef ->
                Log.d(TAG, "Reseña creada con ID: ${docRef.id}")
                onSuccess(review.copy(id = docRef.id))
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error creando reseña: ${e.message}", e)
                onError(e)
            }
    }

    fun getAllReviews(
        onSuccess: (List<Review>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        Firebase.firestore.collection(COLLECTION)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val reviews = snapshot.documents.mapNotNull { Review.fromDocument(it) }
                Log.d(TAG, "Cargadas ${reviews.size} reseñas")
                onSuccess(reviews)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error cargando reseñas: ${e.message}", e)
                onError(e)
            }
    }

    fun getReviewsByUser(
        userId: String,
        onSuccess: (List<Review>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        Firebase.firestore.collection(COLLECTION)
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val reviews = snapshot.documents.mapNotNull { Review.fromDocument(it) }
                onSuccess(reviews)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error cargando reseñas del usuario: ${e.message}", e)
                onError(e)
            }
    }

    fun getReviewsForMovie(
        movieId: Int,
        onSuccess: (List<Review>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        Firebase.firestore.collection(COLLECTION)
            .whereEqualTo("movieId", movieId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val reviews = snapshot.documents.mapNotNull { Review.fromDocument(it) }
                onSuccess(reviews)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error cargando reseñas de película: ${e.message}", e)
                onError(e)
            }
    }

    fun toggleLike(
        reviewId: String,
        currentLikes: Int,
        onSuccess: (Int) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val newLikes = currentLikes + 1
        Firebase.firestore.collection(COLLECTION)
            .document(reviewId)
            .update("likes", newLikes)
            .addOnSuccessListener { onSuccess(newLikes) }
            .addOnFailureListener { e -> onError(e) }
    }
}
