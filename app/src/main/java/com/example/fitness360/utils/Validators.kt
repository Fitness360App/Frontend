package com.example.fitness360.utils


// Validador de correo electrónico
fun validateMail(email: String, errormsg: String): String? {
    val emailRegex = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
    return if (!emailRegex.matches(email)) errormsg else null
}

// Validador de contraseña
fun validatePassword(password: String, password_exceptions: List<String>): String? {
    return when {
        password.length < 8 -> password_exceptions[0]
        !password.any { it.isLowerCase() } -> password_exceptions[1]
        !password.any { it.isUpperCase() } -> password_exceptions[2]
        !password.any { it.isDigit() } -> password_exceptions[3]
        !password.any { it in "!@#$%^&*(),.?\":{}|<>" } -> password_exceptions[4]
        else -> null
    }
}

// Validador de peso (rango de 30 a 300 kg)
fun validateWeight(weight: String, invalid_weight: String): String? {
    val weightValue = weight.toFloatOrNull()
    return if (weightValue == null || weightValue !in 30f..300f) invalid_weight else null
}

// Validador de altura (rango de 50 a 250 cm)
fun validateHeight(height: String, invalid_height: String): String? {
    val heightValue = height.toIntOrNull()
    return if (heightValue == null || heightValue !in 50..250) invalid_height else null
}

// Validador de edad (rango de 10 a 120 años)
fun validateAge(age: String, invalid_age: String): String? {
    val ageValue = age.toIntOrNull()
    return if (ageValue == null || ageValue !in 10..120) invalid_age else null
}