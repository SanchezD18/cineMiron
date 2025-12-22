package com.example.cinemiron.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EditProfileDialog(
    onDismiss: () -> Unit,
    onSave: (bio: String, ubicacion: String, fotoUrl: String, perfilPublico: Boolean) -> Unit,
    initialBio: String = "",
    initialUbicacion: String = "",
    initialFotoUrl: String = "",
    initialPerfilPublico: Boolean = false,
    isLoading: Boolean = false
) {
    var bio by remember { mutableStateOf(initialBio) }
    var ubicacion by remember { mutableStateOf(initialUbicacion) }
    var fotoUrl by remember { mutableStateOf(initialFotoUrl) }
    var perfilPublico by remember { mutableStateOf(initialPerfilPublico) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Editar Perfil",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Biografía") },
                    placeholder = { Text("Cuéntanos sobre ti...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                OutlinedTextField(
                    value = ubicacion,
                    onValueChange = { ubicacion = it },
                    label = { Text("Ubicación") },
                    placeholder = { Text("Ej: Madrid, España") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                OutlinedTextField(
                    value = fotoUrl,
                    onValueChange = { fotoUrl = it },
                    label = { Text("URL de foto de perfil") },
                    placeholder = { Text("https://...") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Face,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Perfil público",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (perfilPublico) "Visible para todos" else "Solo tú puedes verlo",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = perfilPublico,
                        onCheckedChange = { perfilPublico = it }
                    )
                }

                if (perfilPublico) {
                    Text(
                        text = "⚠️ Tu perfil será visible para todos los usuarios",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(bio, ubicacion, fotoUrl, perfilPublico)
                },
                enabled = !isLoading
            ) {
                Text(if (isLoading) "Guardando..." else "Guardar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancelar")
            }
        },
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}







