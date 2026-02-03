package com.example.cinemiron.ui.auth.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch

private const val TAG = "LoginScreen"

@Composable
fun LoginScreen(
    navController: NavController,
    modifier: Modifier,
    auth: FirebaseAuth
) {
    var usernameOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var rememberCredentials by remember { mutableStateOf(false) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    
    val isButtonEnabled by remember {
        derivedStateOf {
            usernameOrEmail.isNotBlank() && password.isNotBlank() && !isLoading
        }
    }
    
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(message)
                errorMessage = null
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "CineMirón",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Inicia sesión para continuar",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = usernameOrEmail,
            onValueChange = { usernameOrEmail = it },
            label = { Text("Usuario o Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                TextButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(
                        text = if (passwordVisible) "Ocultar" else "Mostrar",
                        fontSize = 12.sp
                    )
                }
            },
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Checkbox(
                checked = rememberCredentials,
                onCheckedChange = { rememberCredentials = it },
                enabled = !isLoading
            )
            Text(
                text = "Mantener sesión iniciada",
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        TextButton(
            onClick = {
                navController.navigate("resetpassword")
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("¿Has olvidado tu contraseña?")
        }
        Spacer(modifier = Modifier.height(6.dp))

        Button(
            onClick = {
                if (isButtonEnabled) {
                    isLoading = true
                    loginUser(
                        usernameOrEmail = usernameOrEmail.trim(),
                        password = password,
                        auth = auth,
                        onSuccess = {
                            isLoading = false
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onError = { error ->
                            isLoading = false
                            errorMessage = error
                        }
                    )
                }
            },
            enabled = isButtonEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = Color(0xFFCCCCCC),
                disabledContentColor = Color(0xFF666666)
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.height(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = "Iniciar Sesión",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(
            onClick = {
                navController.navigate("register")
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("¿No tienes cuenta? Regístrate")
        }
        
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.padding(16.dp)
        )
    }
}


fun loginUser(
    usernameOrEmail: String,
    password: String,
    auth: FirebaseAuth,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val isEmail = usernameOrEmail.contains("@")
    
    if (isEmail) {
        loginWithEmail(usernameOrEmail, password, auth, onSuccess, onError)
    } else {
        findEmailByUsername(usernameOrEmail) { email ->
            if (email != null) {
                loginWithEmail(email, password, auth, onSuccess, onError)
            } else {
                onError("Usuario no encontrado")
            }
        }
    }
}

fun findEmailByUsername(
    username: String,
    onResult: (String?) -> Unit
) {
    Firebase.firestore.collection("users")
        .whereEqualTo("basicInfo.username", username)
        .limit(1)
        .get()
        .addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val doc = querySnapshot.documents.first()
                val data = doc.data

                val basicInfo = data?.get("basicInfo") as? Map<*, *>
                val email = basicInfo?.get("email") as? String
                
                if (email != null && email.isNotEmpty()) {
                    onResult(email)
                } else {
                    val emailFallback = data?.get("email") as? String ?: doc.getString("email")
                    if (emailFallback != null && emailFallback.isNotEmpty()) {
                        onResult(emailFallback)
                    } else {
                        onResult(null)
                    }
                }
            } else {
                Firebase.firestore.collection("users")
                    .whereEqualTo("username", username)
                    .limit(1)
                    .get()
                    .addOnSuccessListener { oldQuerySnapshot ->
                        if (!oldQuerySnapshot.isEmpty) {
                            val doc = oldQuerySnapshot.documents.first()
                            val email = doc.getString("email")
                            if (email != null && email.isNotEmpty()) {
                                onResult(email)
                            } else {
                                onResult(null)
                            }
                        } else {
                            onResult(null)
                        }
                    }
                    .addOnFailureListener { e ->
                        onResult(null)
                    }
            }
        }
        .addOnFailureListener { e ->
            Firebase.firestore.collection("users")
                .whereEqualTo("username", username)
                .limit(1)
                .get()
                .addOnSuccessListener { oldQuerySnapshot ->
                    if (!oldQuerySnapshot.isEmpty) {
                        val email = oldQuerySnapshot.documents.first().getString("email")
                        if (email != null && email.isNotEmpty()) {
                        } else {
                            onResult(null)
                        }
                    } else {
                        onResult(null)
                    }
                }
                .addOnFailureListener { e2 ->
                    onResult(null)
                }
        }
}

fun loginWithEmail(
    email: String,
    password: String,
    auth: FirebaseAuth,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    if (email.isEmpty() || email.isBlank()) {
        onError("Email inválido")
        return
    }
    
    if (password.isEmpty() || password.isBlank()) {
        onError("Contraseña inválida")
        return
    }

    auth.signInWithEmailAndPassword(email.trim(), password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user != null) {
                    updateLastLogin(user.uid) {
                        onSuccess()
                    }
                } else {
                    onError("Error al obtener información del usuario")
                }
            } else {
                val exception = task.exception
                val errorMessage = exception?.message ?: "Error desconocido al iniciar sesión"

                val friendlyError = when {
                    errorMessage.contains("invalid-email", ignoreCase = true) -> 
                        "El formato del email no es válido"
                    errorMessage.contains("user-disabled", ignoreCase = true) -> 
                        "Esta cuenta ha sido deshabilitada"
                    errorMessage.contains("user-not-found", ignoreCase = true) -> 
                        "No existe una cuenta con este email"
                    errorMessage.contains("wrong-password", ignoreCase = true) -> 
                        "La contraseña es incorrecta"
                    errorMessage.contains("invalid-credential", ignoreCase = true) -> 
                        "Email o contraseña incorrectos"
                    else -> "Error al iniciar sesión: $errorMessage"
                }
                onError(friendlyError)
            }
        }
}

fun updateLastLogin(
    userId: String,
    onComplete: () -> Unit
) {
    Firebase.firestore.collection("users").document(userId)
        .update("lastLogin", FieldValue.serverTimestamp())
        .addOnSuccessListener {
            onComplete()
        }
        .addOnFailureListener { e ->
            onComplete()
        }
}