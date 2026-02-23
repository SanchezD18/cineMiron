package com.example.cinemiron.domain.repository



import com.example.cinemiron.data.local.models.local.models.UserBasicInfo
import com.example.cinemiron.data.local.models.local.models.UserProfile
import com.example.cinemiron.data.local.models.local.models.UserProfileInfo
import com.example.cinemiron.data.local.models.local.models.UserSettings

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ProfileRepository {

    private val usersCollection = firestore.collection("users")

    override suspend fun loadUserProfile(userId: String): UserProfile? {
        if (userId.isEmpty()) return null
        val document = usersCollection.document(userId).get().await()
        return if (document.exists()) {
            UserProfile.fromDocument(document)
        } else {
            // Si no existe, devolver un perfil vacío
            UserProfile(
                userId = userId,
                basicInfo = UserBasicInfo(),
                profileInfo = UserProfileInfo(),
                settings = UserSettings()
            )
        }
    }

    override suspend fun updateUserProfileInfo(
        userId: String,
        bio: String,
        ubicacion: String,
        fotoUrl: String,
        perfilPublico: Boolean
    ) {
        if (userId.isEmpty()) return
        val profileInfoMap = hashMapOf<String, Any>(
            "bio" to bio.trim(),
            "fotoUrl" to fotoUrl.trim(),
            "ubicacion" to ubicacion.trim(),
            "perfilPublico" to perfilPublico,
            "ultimaActualizacion" to FieldValue.serverTimestamp()
        )
        val updates = hashMapOf<String, Any>("profileInfo" to profileInfoMap)
        usersCollection.document(userId).update(updates).await()
    }

    override suspend fun saveUserProfileToFirestore(
        userId: String,
        email: String,
        username: String
    ) {
        val trimmedUsername = username.trim()
        val trimmedEmail = email.trim()
        if (userId.isEmpty() || trimmedUsername.isBlank()) {
            throw Exception("ID de usuario o nombre de usuario inválido")
        }

        // Verificar si el username ya está en uso por otro usuario
        val querySnapshot = usersCollection
            .whereEqualTo("basicInfo.username", trimmedUsername)
            .get()
            .await()
        if (!querySnapshot.isEmpty) {
            val existingDoc = querySnapshot.documents.first()
            if (existingDoc.id != userId) {
                throw Exception("El nombre de usuario ya está en uso")
            }
        }

        val basicInfoMap = hashMapOf<String, Any>(
            "email" to trimmedEmail,
            "username" to trimmedUsername,
            "fechaRegistro" to FieldValue.serverTimestamp()
        )

        val profileInfoMap = hashMapOf<String, Any>(
            "bio" to "",
            "fotoUrl" to "",
            "ubicacion" to "",
            "perfilPublico" to false,
            "ultimaActualizacion" to FieldValue.serverTimestamp()
        )

        val settingsMap = hashMapOf<String, Any>(
            "tema" to "sistema",
            "notificaciones" to true,
            "colorPrimario" to "#2196F3",
            "idioma" to "es"
        )

        val userData = hashMapOf<String, Any>(
            "basicInfo" to basicInfoMap,
            "profileInfo" to profileInfoMap,
            "settings" to settingsMap
        )

        usersCollection.document(userId).set(userData).await()
    }
}