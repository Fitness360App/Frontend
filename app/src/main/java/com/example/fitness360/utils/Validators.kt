package com.example.fitness360.utils

// Validador de correo electrónico
fun validateMail(email: String): String? {
    val emailRegex = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
    return if (!emailRegex.matches(email)) "Correo electrónico no válido" else null
}

// Validador de contraseña
fun validatePassword(password: String): String? {
    return when {
        password.length < 8 -> "La contraseña debe tener al menos 8 caracteres."
        !password.any { it.isLowerCase() } -> "Debe contener al menos una letra minúscula."
        !password.any { it.isUpperCase() } -> "Debe contener al menos una letra mayúscula."
        !password.any { it.isDigit() } -> "Debe contener al menos un número."
        !password.any { it in "!@#$%^&*(),.?\":{}|<>" } -> "Debe contener al menos un carácter especial."
        else -> null
    }
}

// Validador de peso (rango de 30 a 300 kg)
fun validateWeight(weight: String): String? {
    val weightValue = weight.toFloatOrNull()
    return if (weightValue == null || weightValue !in 30f..300f) "Peso inválido (30-300 kg)" else null
}

// Validador de altura (rango de 50 a 250 cm)
fun validateHeight(height: String): String? {
    val heightValue = height.toIntOrNull()
    return if (heightValue == null || heightValue !in 50..250) "Altura inválida (50-250 cm)" else null
}

// Validador de edad (rango de 10 a 120 años)
fun validateAge(age: String): String? {
    val ageValue = age.toIntOrNull()
    return if (ageValue == null || ageValue !in 10..120) "Edad inválida (10-120 años)" else null
}