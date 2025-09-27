package com.interbank.app.util

/**
 * Reglas:
 * - DNI Perú: exactamente 8 dígitos.
 * - Contraseña: solo letras (A-Z, a-z) y dígitos (0-9), al menos 1 caracter.
 */
object Validators {
    private val dniRegex = Regex("^[0-9]{8}$")
    private val passRegex = Regex("^[A-Za-z0-9]+$")

    fun isValidDni(text: String): Boolean = dniRegex.matches(text.trim())
    fun isValidPassword(text: String): Boolean = text.isNotEmpty() && passRegex.matches(text)
}
