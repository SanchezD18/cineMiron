package com.example.cinemiron.data.local.repository

import android.util.Log
import com.example.cinemiron.data.local.models.local.models.UserBasicInfo
import com.example.cinemiron.data.local.models.local.models.UserProfile
import com.example.cinemiron.data.local.models.local.models.UserProfileInfo
import com.example.cinemiron.data.local.models.local.models.UserSettings
import com.google.firebase.firestore.FieldValue
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

private const val TAG = "UserRepository"

fun loadUserProfile(
    userId: String,
    onSuccess: (UserProfile) -> Unit,
    onError: (Exception) -> Unit
) {
    if (userId.isEmpty()) {
        onError(Exception("ID de usuario inválido"))
        return
    }

    Log.d(TAG, "Cargando perfil para usuario: $userId")

    Firebase.firestore.collection("users").document(userId)
        .get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                Log.d(TAG, "Documento encontrado para usuario: $userId")
                Log.d(TAG, "Datos del documento: ${document.data}")
                val profile = UserProfile.fromDocument(document)
                if (profile != null) {
                    Log.d(TAG, "✅ Perfil cargado exitosamente para: $userId")
                    Log.d(TAG, "  - Nombre: ${profile.basicInfo.nombre}")
                    Log.d(TAG, "  - Email: ${profile.basicInfo.email}")
                    Log.d(TAG, "  - Fecha registro: ${profile.basicInfo.fechaRegistro}")
                    onSuccess(profile)
                } else {
                    Log.e(TAG, "Error: No se pudo parsear el perfil del documento")
                    Log.e(TAG, "Datos del documento: ${document.data}")
                    onError(Exception("Error al parsear los datos del perfil"))
                }
            } else {
                Log.w(TAG, "Documento no existe para usuario: $userId")
                // Crear perfil vacío si no existe
                val emptyProfile = UserProfile(
                    userId = userId,
                    basicInfo = UserBasicInfo(),
                    profileInfo = UserProfileInfo(),
                    settings = UserSettings()
                )
                onSuccess(emptyProfile)
            }
        }
        .addOnFailureListener { e ->
            Log.e(TAG, "❌ Error cargando perfil para $userId: ${e.message}", e)
            onError(e)
        }
}

fun updateUserProfileInfo(
    userId: String,
    bio: String,
    ubicacion: String,
    fotoUrl: String,
    perfilPublico: Boolean,
    onSuccess: () -> Unit,
    onError: (Exception) -> Unit
) {
    if (userId.isEmpty()) {
        onError(Exception("ID de usuario inválido"))
        return
    }

    Log.d(TAG, "Actualizando profileInfo para usuario: $userId")

    val profileInfoMap = hashMapOf<String, Any>(
        "bio" to bio.trim(),
        "fotoUrl" to fotoUrl.trim(),
        "ubicacion" to ubicacion.trim(),
        "perfilPublico" to perfilPublico,
        "ultimaActualizacion" to FieldValue.serverTimestamp()
    )

    val updates = hashMapOf<String, Any>(
        "profileInfo" to profileInfoMap
    )

    Firebase.firestore.collection("users").document(userId)
        .update(updates)
        .addOnSuccessListener {
            Log.d(TAG, "✅ ProfileInfo actualizado exitosamente para: $userId")
            onSuccess()
        }
        .addOnFailureListener { e ->
            Log.e(TAG, "❌ Error actualizando profileInfo para $userId: ${e.message}", e)
            onError(e)
        }
}

fun saveUserProfileToFirestore(
    userId: String,
    email: String,
    username: String,
    onSuccess: () -> Unit = {},
    onError: (Exception) -> Unit = {}
) {
    val trimmedUsername = username.trim()
    val trimmedEmail = email.trim()
    
    if (userId.isEmpty()) {
        onError(Exception("ID de usuario inválido"))
        return
    }

    if (trimmedUsername.isBlank()) {
        onError(Exception("El nombre de usuario es requerido"))
        return
    }

    Log.d(TAG, "Intentando guardar perfil en Firestore con nueva estructura:")
    Log.d(TAG, "  - UserId: $userId")
    Log.d(TAG, "  - Email: $trimmedEmail")
    Log.d(TAG, "  - Username: $trimmedUsername")

    Firebase.firestore.collection("users")
        .whereEqualTo("basicInfo.username", trimmedUsername)
        .get()
        .addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val existingDoc = querySnapshot.documents.first()
                if (existingDoc.id != userId) {
                    Log.w(TAG, "Username '$trimmedUsername' ya está en uso por otro usuario: ${existingDoc.id}")
                    onError(Exception("El nombre de usuario ya está en uso"))
                    return@addOnSuccessListener
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

            Firebase.firestore.collection("users").document(userId)
                .set(userData)
                .addOnSuccessListener {
                    Log.d(TAG, "✅ Perfil guardado exitosamente en Firestore para: $userId")
                    Log.d(TAG, "  - Email: $trimmedEmail")
                    Log.d(TAG, "  - Username: $trimmedUsername")
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "❌ Error guardando perfil en Firestore para $userId: ${e.message}", e)

                    if (e.message?.contains("PERMISSION_DENIED") == true || 
                        e.message?.contains("Missing or insufficient permissions") == true) {
                        onError(Exception("Error de permisos: Configura las reglas de Firestore en Firebase Console para permitir escritura en la colección 'users'"))
                    } else {
                        onError(e)
                    }
                }
        }
        .addOnFailureListener { e ->
            Log.e(TAG, "Error en verificación final de username: ${e.message}", e)

            if (e.message?.contains("PERMISSION_DENIED") == true || 
                e.message?.contains("Missing or insufficient permissions") == true) {
                onError(Exception("Error de permisos en Firestore. Configura las reglas de seguridad en Firebase Console."))
            } else {
                onError(e)
            }
        }
}

