package com.example.cinemiron.ui.review

import androidx.lifecycle.ViewModel
import com.example.cinemiron.data.local.models.local.models.Review
import com.example.cinemiron.data.local.repository.ReviewRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ReviewScreenState(
    val allReviews: List<Review> = emptyList(),
    val myReviews: List<Review> = emptyList(),
    val movieReviews: List<Review> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)

class ReviewViewModel : ViewModel() {

    private val _state = MutableStateFlow(ReviewScreenState())
    val state = _state.asStateFlow()

    private val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    private val currentUserName: String
        get() = FirebaseAuth.getInstance().currentUser?.displayName
            ?: FirebaseAuth.getInstance().currentUser?.email?.substringBefore("@")
            ?: "Usuario"

    fun loadAllReviews() {
        _state.update { it.copy(isLoading = true, error = null) }
        ReviewRepository.getAllReviews(
            onSuccess = { reviews ->
                _state.update { it.copy(allReviews = reviews, isLoading = false) }
            },
            onError = { e ->
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        )
    }

    fun loadMyReviews() {
        val uid = currentUserId ?: return
        _state.update { it.copy(isLoading = true, error = null) }
        ReviewRepository.getReviewsByUser(
            userId = uid,
            onSuccess = { reviews ->
                _state.update { it.copy(myReviews = reviews, isLoading = false) }
            },
            onError = { e ->
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        )
    }

    fun loadReviewsForMovie(movieId: Int) {
        _state.update { it.copy(isLoading = true, error = null) }
        ReviewRepository.getReviewsForMovie(
            movieId = movieId,
            onSuccess = { reviews ->
                _state.update { it.copy(movieReviews = reviews, isLoading = false) }
            },
            onError = { e ->
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        )
    }

    fun submitReview(
        movieId: Int,
        movieTitle: String,
        moviePosterPath: String,
        rating: Int,
        reviewTitle: String,
        reviewDescription: String,
        hasSpoiler: Boolean
    ) {
        val uid = currentUserId ?: return
        _state.update { it.copy(isSaving = true, saveSuccess = false, error = null) }

        val review = Review(
            userId = uid,
            userName = currentUserName,
            movieId = movieId,
            movieTitle = movieTitle,
            moviePosterPath = moviePosterPath,
            rating = rating,
            reviewTitle = reviewTitle,
            reviewDescription = reviewDescription,
            hasSpoiler = hasSpoiler
        )

        ReviewRepository.addReview(
            review = review,
            onSuccess = {
                _state.update { it.copy(isSaving = false, saveSuccess = true) }
                loadAllReviews()
                loadMyReviews()
            },
            onError = { e ->
                _state.update { it.copy(isSaving = false, error = e.message) }
            }
        )
    }

    fun onLikeClicked(review: Review) {
        ReviewRepository.toggleLike(
            reviewId = review.id,
            currentLikes = review.likes,
            onSuccess = { newLikes ->
                _state.update { state ->
                    state.copy(
                        allReviews = state.allReviews.map {
                            if (it.id == review.id) it.copy(likes = newLikes) else it
                        },
                        myReviews = state.myReviews.map {
                            if (it.id == review.id) it.copy(likes = newLikes) else it
                        }
                    )
                }
            },
            onError = { }
        )
    }

    fun clearSaveSuccess() {
        _state.update { it.copy(saveSuccess = false) }
    }
}
