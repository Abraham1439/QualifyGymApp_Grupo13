package com.example.qualifygym_grupo13.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import com.example.qualifygym_grupo13.domain.validation.validateStrongPassword
import com.example.qualifygym_grupo13.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//Pantalla para cambiar la contraseña
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    authViewModel: AuthViewModel? = null,
    onPasswordChanged: () -> Unit,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showCurrentPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var currentPasswordError by remember { mutableStateOf<String?>(null) }
    var newPasswordErrors by remember { mutableStateOf<List<String>>(emptyList()) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    // Validar nueva contraseña en tiempo real
    LaunchedEffect(newPassword) {
        if (newPassword.isNotEmpty()) {
            newPasswordErrors = validateStrongPassword(newPassword)
        } else {
            newPasswordErrors = emptyList()
        }
    }

    // Validar confirmación en tiempo real
    LaunchedEffect(confirmPassword, newPassword) {
        confirmPasswordError = if (confirmPassword.isNotEmpty() && confirmPassword != newPassword) {
            "Las contraseñas no coinciden"
        } else {
            null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cambiar Contraseña") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
        
        Spacer(Modifier.height(24.dp))

        // Mensajes de error
        if (errorMessage.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = errorMessage,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            Spacer(Modifier.height(16.dp))
        }

            // Campo para la contraseña actual
            OutlinedTextField(
                value = currentPassword,
                onValueChange = { 
                    currentPassword = it
                    errorMessage = ""
                    currentPasswordError = null
                },
                label = { Text("Contraseña Actual") },
                singleLine = true,
                visualTransformation = if (showCurrentPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                isError = currentPasswordError != null,
                trailingIcon = {
                    IconButton(onClick = { showCurrentPassword = !showCurrentPassword }) {
                        Icon(
                            imageVector = if (showCurrentPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (showCurrentPassword) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                supportingText = {
                    if (currentPasswordError != null) {
                        Text(
                            text = currentPasswordError!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
            Spacer(Modifier.height(16.dp))

            // Campo para la nueva contraseña
            OutlinedTextField(
                value = newPassword,
                onValueChange = { 
                    newPassword = it
                    errorMessage = ""
                },
                label = { Text("Nueva Contraseña") },
                singleLine = true,
                visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                isError = newPasswordErrors.isNotEmpty(),
                trailingIcon = {
                    IconButton(onClick = { showNewPassword = !showNewPassword }) {
                        Icon(
                            imageVector = if (showNewPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (showNewPassword) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                }
            )
            
            // Mostrar errores de validación de contraseña
            if (newPasswordErrors.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp)
                ) {
                    newPasswordErrors.forEach { error ->
                        Text(
                            text = "La contraseña $error",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(16.dp))

            // Campo para confirmar la nueva contraseña
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { 
                    confirmPassword = it
                    errorMessage = ""
                },
                label = { Text("Confirmar Nueva Contraseña") },
                singleLine = true,
                visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                isError = confirmPasswordError != null,
                trailingIcon = {
                    IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                        Icon(
                            imageVector = if (showConfirmPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (showConfirmPassword) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                supportingText = {
                    if (confirmPasswordError != null) {
                        Text(
                            text = confirmPasswordError!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
            Spacer(Modifier.height(24.dp))

            // Botón para confirmar el cambio
            Button(
                onClick = {
                    // Limpiar mensajes previos
                    errorMessage = ""
                    currentPasswordError = null
                    
                    // Validaciones
                    if (currentPassword.isEmpty()) {
                        errorMessage = "Debe ingresar su contraseña actual"
                        return@Button
                    }
                    
                    if (newPassword.isEmpty()) {
                        errorMessage = "Debe ingresar una nueva contraseña"
                        return@Button
                    }
                    
                    if (newPasswordErrors.isNotEmpty()) {
                        errorMessage = "La nueva contraseña no cumple con los requisitos de seguridad"
                        return@Button
                    }
                    
                    if (confirmPassword.isEmpty()) {
                        errorMessage = "Debe confirmar la nueva contraseña"
                        return@Button
                    }
                    
                    if (newPassword != confirmPassword) {
                        errorMessage = "Las contraseñas no coinciden"
                        return@Button
                    }
                    
                    if (currentPassword == newPassword) {
                        errorMessage = "La nueva contraseña debe ser diferente a la actual"
                        return@Button
                    }

                    // Procesar cambio de contraseña
                    scope.launch {
                        isLoading = true
                        
                        val result = authViewModel?.changeUserPassword(currentPassword, newPassword)
                        
                        isLoading = false
                        
                        if (result?.isSuccess == true) {
                            Toast.makeText(context, "Contraseña cambiada con exito", Toast.LENGTH_SHORT).show()
                            delay(1500)
                            onPasswordChanged()
                        } else {
                            val errorMsg = result?.exceptionOrNull()?.message ?: "Error al cambiar la contraseña"
                            if (errorMsg.contains("contraseña actual no coincide")) {
                                currentPasswordError = errorMsg
                                // No mostrar este error en el cuadro rojo, solo debajo del campo de contraseña actual
                                errorMessage = ""
                            } else {
                                // Solo mostrar otros errores en el cuadro rojo
                                errorMessage = errorMsg
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Guardar Cambios")
                }
            }
        }
    }
}