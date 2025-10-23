package com.example.qualifygym_grupo13.domain.validation

import android.util.Patterns

// Valida que el email no esté vacío y cumpla patrón de email
fun validateEmail(email: String): String? {                            // Retorna String? (mensaje) o null si está OK
    if (email.isBlank()) return "El email es obligatorio"              // Regla 1: no vacío
    val ok = Patterns.EMAIL_ADDRESS.matcher(email).matches()           // Regla 2: coincide con patrón de email
    return if (!ok) "Formato de email inválido" else null              // Si no cumple, devolvemos mensaje
}

// Valida que el nombre contenga solo letras y espacios (sin números)
fun validateNameLettersOnly(name: String): String? {                   // Valida nombre
    if (name.isBlank()) return "El nombre es obligatorio"              // Regla 1: no vacío
    val regex = Regex("^[A-Za-zÁÉÍÓÚÑáéíóúñ ]+$")                      // Regla 2: solo letras y espacios (con tildes/ñ)
    return if (!regex.matches(name)) "Solo letras y espacios" else null// Mensaje si falla
}

// Valida que el teléfono tenga solo dígitos y una longitud razonable
fun validatePhoneDigitsOnly(phone: String): String? {                  // Valida teléfono
    if (phone.isBlank()) return "El teléfono es obligatorio"           // Regla 1: no vacío
    if (!phone.all { it.isDigit() }) return "Solo números"             // Regla 2: todos dígitos
    if (phone.length !in 8..9) return "Debe tener entre 9 y 9 dígitos" // Regla 3: tamaño razonable
    return null                                                        // OK
}


// Valida seguridad de la contraseña (mín. 8, mayús, minús, número y símbolo; sin espacios)
fun validateStrongPassword(pass: String): List<String> { //Se cambia esto para que devulva una lista de errores para que no tire los erres uno por uno
    val errors = mutableListOf<String>() //Lista de errores

    // Requisitos mínimos de seguridad
    if (pass.isBlank()) {
        errors.add("La contraseña es obligatoria")
        return errors
    }
    if (pass.length < 8) {
        errors.add("Debe teber mínimo 8 caracteres") // Largo mínimo
    }
    if (!pass.any { it.isUpperCase() }) {
        errors.add("Debe incluir una mayúscula") // Al menos 1 mayúscula
    }
    if (!pass.any { it.isLowerCase() }) {
        errors.add("Debe incluir una minúscula") // Al menos 1 minúscula
    }
    if (!pass.any { it.isDigit() }) {
        errors.add("Debe incluir un número") // Al menos 1 número
    }
    if (!pass.any { !it.isLetterOrDigit() }) {
        errors.add("Debe incluir un símbolo") // Al menos 1 símbolo

    }
    if (pass.contains(' ')) {
        errors.add("No debe contener espacios") // Sin espacios
    }
    return errors     //ya no devulve ok sino la lista de errores
}

// Valida que la confirmación coincida con la contraseña
fun validateConfirm(pass: String, confirm: String): String? {          // Confirmación de contraseña
    if (confirm.isBlank()) return "Confirma tu contraseña"             // No vacío
    return if (pass != confirm) "Las contraseñas no coinciden" else null // Deben ser iguales
}