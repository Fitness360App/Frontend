package com.example.fitness360.utils

import com.example.fitness360.network.Macros

fun calculateMacros(weight: Int, height: Int, age: Int, gender: String, activityLevel: String): Macros {
    val bmr = if (gender == "Hombre" || gender == "Male") {
        10 * weight + 6.25 * height - 5 * age + 5
    } else {
        10 * weight + 6.25 * height - 5 * age - 161
    }

    val activityMultiplier = when (activityLevel) {
        "Sedentario", "Sedentary" -> 1.2
        "Ligera", "Light" -> 1.375
        "Moderada", "Moderate" -> 1.55
        "Alta", "High" -> 1.725
        else -> 1.2
    }

    val tdee = bmr * activityMultiplier

    val protein = (weight * 2).toInt()  // 2 gramos de proteína por kg de peso
    val fat = (weight * 0.8).toInt()    // 0.8 gramos de grasa por kg de peso
    val carbs = (tdee - (protein * 4 + fat * 9)) / 4  // El resto de las calorías para carbohidratos

    return Macros(protein, carbs.toInt(), fat, tdee.toInt())
}