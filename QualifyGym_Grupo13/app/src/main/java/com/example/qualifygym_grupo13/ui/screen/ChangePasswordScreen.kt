package com.example.qualifygym_grupo13.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//Pantalla para cambiar la contraseña
@Composable
fun ChangePasswordScreen(
    onPasswordChanged: () -> Unit, //Función para cuando se complete el cambio
    onBack: () -> Unit = onPasswordChanged // Función para volver atrás
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showCurrentPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    var shouldProcessChange by remember { mutableStateOf(false) }

    // Validaciones
    val isFormValid = currentPassword.isNotEmpty() && 
                     newPassword.isNotEmpty() && 
                     confirmPassword.isNotEmpty() &&
                     newPassword == confirmPassword &&
                     newPassword.length >= 6

    // Efecto para procesar el cambio de contraseña
    LaunchedEffect(shouldProcessChange) {
        if (shouldProcessChange) {
            delay(1500) // Simular llamada a API
            isLoading = false
            successMessage = "Contraseña cambiada exitosamente"
            
            // Limpiar campos después de un tiempo
            delay(2000)
            onPasswordChanged()
            shouldProcessChange = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header con botón de regreso
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Cambiar Contraseña", 
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(Modifier.height(24.dp))

        // Mensajes de error y éxito
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

        if (successMessage.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = successMessage,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
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
                successMessage = ""
            },
            label = { Text("Contraseña Actual") },
            singleLine = true,
            visualTransformation = if (showCurrentPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { showCurrentPassword = !showCurrentPassword }) {
                    Icon(
                        imageVector = if (showCurrentPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (showCurrentPassword) "Ocultar contraseña" else "Mostrar contraseña"
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
                successMessage = ""
            },
            label = { Text("Nueva Contraseña") },
            singleLine = true,
            visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { showNewPassword = !showNewPassword }) {
                    Icon(
                        imageVector = if (showNewPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (showNewPassword) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            },
            supportingText = {
                if (newPassword.isNotEmpty() && newPassword.length < 6) {
                    Text("La contraseña debe tener al menos 6 caracteres")
                }
            }
        )
        Spacer(Modifier.height(16.dp))

        // Campo para confirmar la nueva contraseña
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { 
                confirmPassword = it
                errorMessage = ""
                successMessage = ""
            },
            label = { Text("Confirmar Nueva Contraseña") },
            singleLine = true,
            visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                    Icon(
                        imageVector = if (showConfirmPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (showConfirmPassword) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            },
            supportingText = {
                if (confirmPassword.isNotEmpty() && newPassword != confirmPassword) {
                    Text("Las contraseñas no coinciden")
                }
            }
        )
        Spacer(Modifier.height(24.dp))

        // Botón para confirmar el cambio
        Button(
            onClick = {
                // Validaciones adicionales
                if (currentPassword.isEmpty()) {
                    errorMessage = "Debe ingresar su contraseña actual"
                    return@Button
                }
                if (newPassword.length < 6) {
                    errorMessage = "La nueva contraseña debe tener al menos 6 caracteres"
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

                // Simular proceso de cambio de contraseña
                isLoading = true
                errorMessage = ""
                shouldProcessChange = true
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormValid && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(if (isLoading) "Guardando..." else "Guardar Cambios")
        }
    }
}