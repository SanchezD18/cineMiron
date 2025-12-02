package com.example.cinemiron.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun LoginScreen(navController: NavController, modifier: Modifier) {
    var usernameText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    val isButtonEnabled by remember {
        derivedStateOf {
            usernameText.isNotBlank() && passwordText.isNotBlank()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "CineMirón"
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Inicia sesión para continuar"
        )
        Spacer(modifier = Modifier.height(30.dp))
        OutlinedTextField(
            value = usernameText,
            onValueChange = { usernameText = it },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = passwordText,
            onValueChange = { passwordText = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        CheckboxMinimalExample()

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate("home")
            },
            enabled = isButtonEnabled,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(

                disabledContainerColor = Color(0xFFCCCCCC),
                disabledContentColor = Color(0xFF666666)

            )
        ) {
            Text("Iniciar Sesión")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                navController.navigate("register")
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(

                disabledContainerColor = Color(0xFFCCCCCC),
                disabledContentColor = Color(0xFF666666)

            )
        ) {
            Text("Registrarse")
        }
    }
}


@Composable
fun CheckboxMinimalExample() {
    var checked by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start

    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { checked = it }
        )
        Text(
            "Recordar credenciales"
        )
    }
}
