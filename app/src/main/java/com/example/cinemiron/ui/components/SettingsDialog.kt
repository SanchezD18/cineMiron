package com.example.cinemiron.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.cinemiron.R
import com.example.cinemiron.ui.theme.ColorSchemeOption


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    initialDarkTheme: Boolean = false,
    initialColorScheme: ColorSchemeOption = ColorSchemeOption.VERDE,
    initialNotificationsEnabled: Boolean = true,
    initialPublicProfile: Boolean = false,
    onThemeChanged: ((Boolean) -> Unit)? = null,
    onColorSchemeChanged: ((ColorSchemeOption) -> Unit)? = null,
    onNotificationsChanged: ((Boolean) -> Unit)? = null,
    onProfileChanged: ((Boolean) -> Unit)? = null,
    onLogout: (() -> Unit)? = null
) {
    var isDarkTheme by remember { mutableStateOf(initialDarkTheme) }
    var selectedColorScheme by remember { mutableStateOf(initialColorScheme) }
    var notificationsEnabled by remember { mutableStateOf(initialNotificationsEnabled) }
    var isPublicProfile by remember { mutableStateOf(initialPublicProfile) }
    var showColorDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Configuración",
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
                SettingItem(
                    icon = Icons.Filled.Settings,
                    title = "Tema de la app",
                    subtitle = if (isDarkTheme) "Modo oscuro" else "Modo claro",
                    checked = isDarkTheme,
                    onCheckedChange = { newValue ->
                        isDarkTheme = newValue
                        onThemeChanged?.invoke(newValue)
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                ColorSchemeSelector(
                    iconPainter = painterResource(id = R.drawable.palette),
                    title = "Esquema de colores",
                    subtitle = getColorSchemeName(selectedColorScheme),
                    selectedColorScheme = selectedColorScheme,
                    onColorSchemeSelected = { newScheme ->
                        selectedColorScheme = newScheme
                        onColorSchemeChanged?.invoke(newScheme)
                    },
                    showDropdown = showColorDropdown,
                    onDropdownToggle = { showColorDropdown = !showColorDropdown }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                SettingItem(
                    icon = Icons.Filled.Notifications,
                    title = "Notificaciones",
                    subtitle = if (notificationsEnabled) "Activadas" else "Desactivadas",
                    checked = notificationsEnabled,
                    onCheckedChange = { newValue ->
                        notificationsEnabled = newValue
                        onNotificationsChanged?.invoke(newValue)
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                onLogout?.let {
                    LogoutButton(
                        onClick = {
                            onDismiss()
                            it()
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}


@Composable
fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
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
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorSchemeSelector(
    iconPainter: Painter,
    title: String,
    subtitle: String,
    selectedColorScheme: ColorSchemeOption,
    onColorSchemeSelected: (ColorSchemeOption) -> Unit,
    showDropdown: Boolean,
    onDropdownToggle: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.TopEnd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = iconPainter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onDropdownToggle) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "Seleccionar color",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        DropdownMenu(
            expanded = showDropdown,
            onDismissRequest = onDropdownToggle,
            modifier = Modifier.wrapContentSize()
        ) {
            ColorSchemeOption.entries.forEach { scheme ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(getColorSchemePreviewColor(scheme))
                            )
                            Text(
                                text = getColorSchemeName(scheme),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    },
                    onClick = {
                        onColorSchemeSelected(scheme)
                        onDropdownToggle()
                    }
                )
            }
        }
    }
}

private fun getColorSchemeName(scheme: ColorSchemeOption): String {
    return when (scheme) {
        ColorSchemeOption.VERDE -> "Verde"
        ColorSchemeOption.AZUL -> "Azul"
        ColorSchemeOption.PURPURA -> "Púrpura"
        ColorSchemeOption.ROJO -> "Rojo"
        ColorSchemeOption.NARANJA -> "Naranja"
        ColorSchemeOption.TEAL -> "Teal"
    }
}

private fun getColorSchemePreviewColor(scheme: ColorSchemeOption): Color {
    return when (scheme) {
        ColorSchemeOption.VERDE -> Color(0xFF506F6A)
        ColorSchemeOption.AZUL -> Color(0xFF283593)
        ColorSchemeOption.PURPURA -> Color(0xFF6A1B9A)
        ColorSchemeOption.ROJO -> Color(0xFFC62828)
        ColorSchemeOption.NARANJA -> Color(0xFFF57C00)
        ColorSchemeOption.TEAL -> Color(0xFF00695C)
    }
}

@Composable
fun LogoutButton(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError
        )
    ) {
        Icon(
            imageVector = Icons.Filled.ExitToApp,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Cerrar sesión")
    }
}

