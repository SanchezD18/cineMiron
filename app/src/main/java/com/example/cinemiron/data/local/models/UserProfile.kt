package com.example.cinemiron.data.local.models.local.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

data class UserBasicInfo(
    val email: String = "",
    val nombre: String = "",
    val fechaRegistro: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "email" to email,
            "username" to nombre,
            "fechaRegistro" to fechaRegistro
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any?>): UserBasicInfo {
            val fechaRegistroValue = map["fechaRegistro"]
            val fechaRegistro = when {
                fechaRegistroValue == null -> null
                fechaRegistroValue is Timestamp -> fechaRegistroValue
                fechaRegistroValue is Map<*, *> -> {
                    try {
                        val seconds = (fechaRegistroValue["seconds"] as? Number)?.toLong() ?: 0L
                        val nanoseconds = (fechaRegistroValue["nanoseconds"] as? Number)?.toInt() ?: 0
                        Timestamp(seconds, nanoseconds)
                    } catch (e: Exception) {
                        null
                    }
                }
                else -> null
            }

            val nombre = map["username"] as? String ?: map["nombre"] as? String ?: ""
            
            return UserBasicInfo(
                email = map["email"] as? String ?: "",
                nombre = nombre,
                fechaRegistro = fechaRegistro
            )
        }
    }
}


data class UserProfileInfo(
    val bio: String = "",
    val fotoUrl: String = "",
    val ubicacion: String = "",
    val perfilPublico: Boolean = false,
    val ultimaActualizacion: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "bio" to bio,
            "fotoUrl" to fotoUrl,
            "ubicacion" to ubicacion,
            "perfilPublico" to perfilPublico,
            "ultimaActualizacion" to ultimaActualizacion
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any?>): UserProfileInfo {
            return UserProfileInfo(
                bio = map["bio"] as? String ?: "",
                fotoUrl = map["fotoUrl"] as? String ?: "",
                ubicacion = map["ubicacion"] as? String ?: "",
                perfilPublico = map["perfilPublico"] as? Boolean ?: false,
                ultimaActualizacion = map["ultimaActualizacion"] as? Timestamp
            )
        }
    }
}


data class UserSettings(
    val tema: String = "sistema",
    val notificaciones: Boolean = true,
    val colorPrimario: String = "#2196F3",
    val idioma: String = "es"
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "tema" to tema,
            "notificaciones" to notificaciones,
            "colorPrimario" to colorPrimario,
            "idioma" to idioma
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any?>): UserSettings {
            return UserSettings(
                tema = map["tema"] as? String ?: "sistema",
                notificaciones = map["notificaciones"] as? Boolean ?: true,
                colorPrimario = map["colorPrimario"] as? String ?: "#2196F3",
                idioma = map["idioma"] as? String ?: "es"
            )
        }
    }
}

data class UserProfile(
    val userId: String,
    val basicInfo: UserBasicInfo = UserBasicInfo(),
    val profileInfo: UserProfileInfo = UserProfileInfo(),
    val settings: UserSettings = UserSettings()
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "basicInfo" to basicInfo.toMap(),
            "profileInfo" to profileInfo.toMap(),
            "settings" to settings.toMap()
        )
    }

    companion object {
        fun fromDocument(document: DocumentSnapshot): UserProfile? {
            val userId = document.id
            val data = document.data ?: return null

            val hasNewStructure = data.containsKey("basicInfo") && 
                                 data.containsKey("profileInfo") && 
                                 data.containsKey("settings")

            return if (hasNewStructure) {
                val basicInfoMap = data["basicInfo"] as? Map<String, Any?> ?: emptyMap()
                val profileInfoMap = data["profileInfo"] as? Map<String, Any?> ?: emptyMap()
                val settingsMap = data["settings"] as? Map<String, Any?> ?: emptyMap()

                UserProfile(
                    userId = userId,
                    basicInfo = UserBasicInfo.fromMap(basicInfoMap),
                    profileInfo = UserProfileInfo.fromMap(profileInfoMap),
                    settings = UserSettings.fromMap(settingsMap)
                )
            } else {

                val email = data["email"] as? String ?: ""
                val nombre = data["username"] as? String ?: data["nombre"] as? String ?: ""
                val createdAt = data["createdAt"] as? Timestamp ?: data["fechaRegistro"] as? Timestamp
                

                val fechaRegistro = when {
                    createdAt != null -> createdAt
                    data["createdAt"] is Map<*, *> -> {
                        val createdAtMap = data["createdAt"] as Map<*, *>
                        try {
                            val seconds = (createdAtMap["seconds"] as? Number)?.toLong() ?: 0L
                            val nanoseconds = (createdAtMap["nanoseconds"] as? Number)?.toInt() ?: 0
                            Timestamp(seconds, nanoseconds)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    else -> null
                }

                UserProfile(
                    userId = userId,
                    basicInfo = UserBasicInfo(
                        email = email,
                        nombre = nombre,
                        fechaRegistro = fechaRegistro
                    ),
                    profileInfo = UserProfileInfo(),
                    settings = UserSettings()
                )
            }
        }
    }
}

