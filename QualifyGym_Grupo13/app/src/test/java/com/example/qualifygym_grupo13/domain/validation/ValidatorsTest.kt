package com.example.qualifygym_grupo13.domain.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ValidatorsTest {

    // ========== Tests para validateEmail ==========//
    @Test
    fun validateEmail_ok() {
        val error = validateEmail("wena@gmail.com")
        assertNull(error)
        // Valida Email con formato correcto
        // Retorna null (sin errores)
    }

    @Test
    fun validateEmail_incorrecto() {
        val error = validateEmail("a.com")
        assertEquals("Formato de email inválido", error)
        // Valida email con formato incorrecto
        // Retorna mensaje de error
    }

    @Test
    fun validateEmail_vacio() {
        val error = validateEmail("")
        assertEquals("El email es obligatorio", error)
        // Valida email vacío
        // Retorna mensaje de obligatorio
    }

    @Test
    fun validateEmail_sinArroba() {
        val error = validateEmail("usuariogmail.com")
        assertEquals("Formato de email inválido", error)
        // Valida email sin símbolo @
        // Retorna mensaje de formato inválido
    }

    @Test
    fun validateEmail_sinDominio() {
        val error = validateEmail("usuario@")
        assertEquals("Formato de email inválido", error)
        // Valida email sin dominio después del @
        // Retorna mensaje de formato inválido
    }

    @Test
    fun validateEmail_soloEspacios() {
        val error = validateEmail("   ")
        assertEquals("El email es obligatorio", error)
        // Valida email solo con espacios
        // Retorna mensaje de obligatorio
    }

    @Test
    fun validateEmail_conSubdominio() {
        val error = validateEmail("usuario@mail.gmail.com")
        assertNull(error)
        // Valida email con subdominio
        // Retorna null (sin errores)
    }

    @Test
    fun validateEmail_conNumerosEnDominio() {
        val error = validateEmail("usuario@mail123.com")
        assertNull(error)
        // Valida email con números en el dominio
        // Retorna null (sin errores)
    }

    @Test
    fun validateEmail_conPuntoYGuion() {
        val error = validateEmail("user.name@domain-name.com")
        assertNull(error)
        // Valida email con punto y guion
        // Retorna null (sin errores)
    }


    // ========== Tests para el telefono ==========//
    @Test
    fun validatePhone_ok() {
        val error = validatePhoneDigitsOnly("123456789")
        assertNull(error)
        // Valida teléfono con 9 dígitos
        // Retorna null (sin errores)
    }

    @Test
    fun validatePhone_error_longitud_corta() {
        val error = validatePhoneDigitsOnly("123456")
        assertEquals("Debe tener 9 dígitos", error)
        // Valida teléfono con menos de 9 dígitos
        // Retorna mensaje de longitud incorrecta
    }

    @Test
    fun validatePhone_error_longitud_larga() {
        val error = validatePhoneDigitsOnly("1234567890")
        assertEquals("Debe tener 9 dígitos", error)
        // Valida teléfono con más de 9 dígitos
        // Retorna mensaje de longitud incorrecta
    }

    @Test
    fun validatePhone_vacio() {
        val error = validatePhoneDigitsOnly("")
        assertEquals("El teléfono es obligatorio", error)
        // Valida teléfono vacío
        // Retorna mensaje de obligatorio
    }

    @Test
    fun validatePhone_conLetras() {
        val error = validatePhoneDigitsOnly("12345678a")
        assertEquals("Solo números", error)
        // Valida teléfono con letras
        // Retorna mensaje de solo números
    }

    @Test
    fun validatePhone_conCaracteresEspeciales() {
        val error = validatePhoneDigitsOnly("123-456-789")
        assertEquals("Solo números", error)
        // Valida teléfono con caracteres especiales
        // Retorna mensaje de solo números
    }

    @Test
    fun validatePhone_soloEspacios() {
        val error = validatePhoneDigitsOnly("   ")
        assertEquals("El teléfono es obligatorio", error)
        // Valida teléfono solo con espacios
        // Retorna mensaje de obligatorio
    }

    @Test
    fun validatePhone_conEspacios() {
        val error = validatePhoneDigitsOnly("123 456 789")
        assertEquals("Solo números", error)
        // Valida teléfono con espacios mezclados
        // Retorna mensaje de solo números
    }

    @Test
    fun validatePhone_unSoloDigito() {
        val error = validatePhoneDigitsOnly("1")
        assertEquals("Debe tener 9 dígitos", error)
        // Valida teléfono con un solo dígito
        // Retorna mensaje de longitud incorrecta
    }

    @Test
    fun validatePhone_soloCeros() {
        val error = validatePhoneDigitsOnly("000000000")
        assertNull(error)
        // Valida teléfono con solo ceros
        // Retorna null (sin errores, es válido)
    }

    @Test
    fun validatePhone_digitosRepetidos() {
        val error = validatePhoneDigitsOnly("111111111")
        assertNull(error)
        // Valida teléfono con dígitos repetidos
        // Retorna null (sin errores, es válido)
    }


    // ========== Tests para validateNameLettersOnly ==========//
    @Test
    fun validateName_ok() {
        val error = validateNameLettersOnly("Juan Pérez")
        assertNull(error)
        // Valida nombre con formato correcto
        // Retorna null (sin errores)
    }

    @Test
    fun validateName_vacio() {
        val error = validateNameLettersOnly("")
        assertEquals("El nombre es obligatorio", error)
        // Valida nombre vacío
        // Retorna mensaje de obligatorio
    }

    @Test
    fun validateName_corto() {
        val error = validateNameLettersOnly("Ana")
        assertEquals("Debe tener al menos 4 caracteres", error)
        // Valida nombre con menos de 4 caracteres
        // Retorna mensaje de longitud mínima
    }

    @Test
    fun validateName_conNumeros() {
        val error = validateNameLettersOnly("Juan123")
        assertEquals("Solo letras y espacios", error)
        // Valida nombre con números
        // Retorna mensaje de solo letras y espacios
    }

    @Test
    fun validateName_conCaracteresEspeciales() {
        val error = validateNameLettersOnly("Juan@Pérez")
        assertEquals("Solo letras y espacios", error)
        // Valida nombre con caracteres especiales
        // Retorna mensaje de solo letras y espacios
    }

    @Test
    fun validateName_conAcentos() {
        val error = validateNameLettersOnly("José María")
        assertNull(error)
        // Valida nombre con acentos
        // Retorna null (sin errores)
    }

    @Test
    fun validateName_conEne() {
        val error = validateNameLettersOnly("Niño")
        assertNull(error)
        // Valida nombre con ñ
        // Retorna null (sin errores)
    }

    @Test
    fun validateName_soloEspacios() {
        val error = validateNameLettersOnly("   ")
        assertEquals("El nombre es obligatorio", error)
        // Valida nombre solo con espacios
        // Retorna mensaje de obligatorio
    }

    @Test
    fun validateName_longitudExacta4() {
        val error = validateNameLettersOnly("Juan")
        assertNull(error)
        // Valida nombre con exactamente 4 caracteres (mínimo)
        // Retorna null (sin errores)
    }

    @Test
    fun validateName_conEspaciosMultiples() {
        val error = validateNameLettersOnly("Juan  María")
        assertNull(error)
        // Valida nombre con múltiples espacios consecutivos
        // Retorna null (sin errores)
    }

    @Test
    fun validateName_soloMayusculas() {
        val error = validateNameLettersOnly("JUAN PÉREZ")
        assertNull(error)
        // Valida nombre solo con mayúsculas
        // Retorna null (sin errores)
    }

    @Test
    fun validateName_soloMinusculas() {
        val error = validateNameLettersOnly("juan pérez")
        assertNull(error)
        // Valida nombre solo con minúsculas
        // Retorna null (sin errores)
    }

    @Test
    fun validateName_muyLargo() {
        val error = validateNameLettersOnly("Juan Carlos María José Pérez García")
        assertNull(error)
        // Valida nombre muy largo
        // Retorna null (sin errores, no hay límite máximo)
    }

    @Test
    fun validateName_unaSolaLetra() {
        val error = validateNameLettersOnly("A")
        assertEquals("Debe tener al menos 4 caracteres", error)
        // Valida nombre con una sola letra
        // Retorna mensaje de longitud mínima
    }


    // ========== Tests para validateStrongPassword ==========//
    @Test
    fun validateStrongPassword_ok() {
        val errors = validateStrongPassword("Password123!")
        assertEquals(0, errors.size)
        // Valida contraseña con todos los requisitos
        // Retorna lista vacía (sin errores)
    }

    @Test
    fun validateStrongPassword_vacio() {
        val errors = validateStrongPassword("")
        assertTrue(errors.contains("La contraseña es obligatoria"))
        // Valida contraseña vacía
        // Retorna lista con error de obligatorio
    }

    @Test
    fun validateStrongPassword_corto() {
        val errors = validateStrongPassword("Pass1!")
        assertTrue(errors.contains("Debe tener mínimo 8 caracteres"))
        // Valida contraseña con menos de 8 caracteres
        // Retorna lista con error de longitud mínima
    }

    @Test
    fun validateStrongPassword_sinMayuscula() {
        val errors = validateStrongPassword("password123!")
        assertTrue(errors.contains("Debe incluir una mayúscula"))
        // Valida contraseña sin mayúsculas
        // Retorna lista con error de mayúscula requerida
    }

    @Test
    fun validateStrongPassword_sinMinuscula() {
        val errors = validateStrongPassword("PASSWORD123!")
        assertTrue(errors.contains("Debe incluir una minúscula"))
        // Valida contraseña sin minúsculas
        // Retorna lista con error de minúscula requerida
    }

    @Test
    fun validateStrongPassword_sinNumero() {
        val errors = validateStrongPassword("Password!")
        assertTrue(errors.contains("Debe incluir un número"))
        // Valida contraseña sin números
        // Retorna lista con error de número requerido
    }

    @Test
    fun validateStrongPassword_sinCaracterEspecial() {
        val errors = validateStrongPassword("Password123")
        assertTrue(errors.contains("Debe incluir un caracter especial"))
        // Valida contraseña sin caracteres especiales
        // Retorna lista con error de carácter especial requerido
    }

    @Test
    fun validateStrongPassword_conEspacios() {
        val errors = validateStrongPassword("Password 123!")
        assertTrue(errors.contains("No debe contener espacios"))
        // Valida contraseña con espacios
        // Retorna lista con error de espacios no permitidos
    }

    @Test
    fun validateStrongPassword_multiplesErrores() {
        val errors = validateStrongPassword("pass")
        assertTrue(errors.size > 1)
        // Valida que retorne múltiples errores
        // Cuando faltan varios requisitos
    }

    @Test
    fun validateStrongPassword_longitudExacta8() {
        val errors = validateStrongPassword("Pass123!")
        assertEquals(0, errors.size)
        // Valida contraseña con exactamente 8 caracteres (mínimo)
        // Retorna lista vacía (sin errores)
    }

    @Test
    fun validateStrongPassword_soloEspacios() {
        val errors = validateStrongPassword("   ")
        assertTrue(errors.contains("La contraseña es obligatoria"))
        // Valida contraseña solo con espacios
        // Retorna lista con error de obligatorio
    }

    @Test
    fun validateStrongPassword_caracterEspecialAlInicio() {
        val errors = validateStrongPassword("!Password123")
        assertEquals(0, errors.size)
        // Valida contraseña con carácter especial al inicio
        // Retorna lista vacía (sin errores)
    }

    @Test
    fun validateStrongPassword_caracterEspecialAlFinal() {
        val errors = validateStrongPassword("Password123!")
        assertEquals(0, errors.size)
        // Valida contraseña con carácter especial al final
        // Retorna lista vacía (sin errores)
    }

    @Test
    fun validateStrongPassword_multiplesCaracteresEspeciales() {
        val errors = validateStrongPassword("Pass!@#123")
        assertEquals(0, errors.size)
        // Valida contraseña con múltiples caracteres especiales
        // Retorna lista vacía (sin errores)
    }

    @Test
    fun validateStrongPassword_muyLarga() {
        val errors = validateStrongPassword("Password123!MuyLarga456")
        assertEquals(0, errors.size)
        // Valida contraseña muy larga
        // Retorna lista vacía (sin errores, no hay límite máximo)
    }

    @Test
    fun validateStrongPassword_soloNumerosYEspeciales() {
        val errors = validateStrongPassword("12345678!")
        assertTrue(errors.contains("Debe incluir una mayúscula"))
        assertTrue(errors.contains("Debe incluir una minúscula"))
        // Valida contraseña solo con números y caracteres especiales
        // Retorna lista con errores de mayúscula y minúscula
    }

    @Test
    fun validateStrongPassword_soloLetrasYEspeciales() {
        val errors = validateStrongPassword("Password!")
        assertTrue(errors.contains("Debe incluir un número"))
        // Valida contraseña solo con letras y caracteres especiales
        // Retorna lista con error de número requerido
    }

    @Test
    fun validateStrongPassword_soloLetrasYNumeros() {
        val errors = validateStrongPassword("Password123")
        assertTrue(errors.contains("Debe incluir un caracter especial"))
        // Valida contraseña solo con letras y números
        // Retorna lista con error de carácter especial requerido
    }

    @Test
    fun validateStrongPassword_espaciosAlInicio() {
        val errors = validateStrongPassword(" Password123!")
        assertTrue(errors.contains("No debe contener espacios"))
        // Valida contraseña con espacios al inicio
        // Retorna lista con error de espacios no permitidos
    }

    @Test
    fun validateStrongPassword_espaciosAlFinal() {
        val errors = validateStrongPassword("Password123! ")
        assertTrue(errors.contains("No debe contener espacios"))
        // Valida contraseña con espacios al final
        // Retorna lista con error de espacios no permitidos
    }


    // ========== Tests para validateConfirm ==========//
    @Test
    fun validateConfirm_ok() {
        val error = validateConfirm("Password123!", "Password123!")
        assertNull(error)
        // Valida que las contraseñas coincidan
        // Retorna null (sin errores)
    }

    @Test
    fun validateConfirm_noCoinciden() {
        val error = validateConfirm("Password123!", "Password456!")
        assertEquals("Las contraseñas no coinciden", error)
        // Valida que las contraseñas no coincidan
        // Retorna mensaje de no coinciden
    }

    @Test
    fun validateConfirm_vacio() {
        val error = validateConfirm("Password123!", "")
        assertEquals("Confirma tu contraseña", error)
        // Valida confirmación de contraseña vacía
        // Retorna mensaje de confirmación requerida
    }

    @Test
    fun validateConfirm_soloEspacios() {
        val error = validateConfirm("Password123!", "   ")
        assertEquals("Confirma tu contraseña", error)
        // Valida confirmación solo con espacios
        // Retorna mensaje de confirmación requerida
    }

    @Test
    fun validateConfirm_passOriginalVacia() {
        val error = validateConfirm("", "Password123!")
        assertEquals("Las contraseñas no coinciden", error)
        // Valida cuando la contraseña original está vacía
        // Retorna mensaje de no coinciden
    }

    @Test
    fun validateConfirm_diferenciaMayusculas() {
        val error = validateConfirm("Password123!", "password123!")
        assertEquals("Las contraseñas no coinciden", error)
        // Valida confirmación con diferencia de mayúsculas/minúsculas
        // Retorna mensaje de no coinciden
    }

    @Test
    fun validateConfirm_diferenciaUnCaracter() {
        val error = validateConfirm("Password123!", "Password123@")
        assertEquals("Las contraseñas no coinciden", error)
        // Valida confirmación con diferencia de un solo carácter
        // Retorna mensaje de no coinciden
    }

    @Test
    fun validateConfirm_confirmacionMasLarga() {
        val error = validateConfirm("Password123!", "Password123!Extra")
        assertEquals("Las contraseñas no coinciden", error)
        // Valida confirmación más larga que la original
        // Retorna mensaje de no coinciden
    }

    @Test
    fun validateConfirm_confirmacionMasCorta() {
        val error = validateConfirm("Password123!", "Password12")
        assertEquals("Las contraseñas no coinciden", error)
        // Valida confirmación más corta que la original
        // Retorna mensaje de no coinciden
    }

    @Test
    fun validateConfirm_espaciosAlInicio() {
        val error = validateConfirm("Password123!", " Password123!")
        assertEquals("Las contraseñas no coinciden", error)
        // Valida confirmación con espacios al inicio
        // Retorna mensaje de no coinciden
    }

    @Test
    fun validateConfirm_espaciosAlFinal() {
        val error = validateConfirm("Password123!", "Password123! ")
        assertEquals("Las contraseñas no coinciden", error)
        // Valida confirmación con espacios al final
        // Retorna mensaje de no coinciden
    }

}