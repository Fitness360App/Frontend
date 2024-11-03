package com.example.fitness360

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitness360.components.BottomNavigationBar

@Composable
fun SettingsScreen(navController: NavController) {
    var showEditDialog by remember { mutableStateOf(false) }

    // Estados para todos los campos de configuración
    var userName by remember { mutableStateOf("Acaymo") }
    var userLastName by remember { mutableStateOf("Granado Sánchez") }
    var userEmail by remember { mutableStateOf("acaElCrack@gmail.com") }
    var currentWeight by remember { mutableStateOf("60") }
    var targetWeight by remember { mutableStateOf("80") }
    var height by remember { mutableStateOf("171") }
    var age by remember { mutableStateOf("20 años") }
    var activityLevel by remember { mutableStateOf("Alto") }
    var language by remember { mutableStateOf("Español (ES)") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 56.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Información del usuario
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = userName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF007ACC)
                    )
                    Text(
                        text = userLastName,
                        fontSize = 20.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Botón de Editar
                Text(
                    text = "Editar",
                    fontSize = 18.sp,
                    color = Color(0xFF007ACC),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable { showEditDialog = true }
                        .padding(horizontal = 8.dp)
                )
            }

            // Opciones de configuración
            SettingsOption("Correo electrónico", userEmail)
            SettingsOption("Peso Actual", "$currentWeight kg")
            SettingsOption("Objetivo de Peso", "$targetWeight kg")
            SettingsOption("Altura", "$height cm")
            SettingsOption("Edad", age)
            SettingsOption("Nivel de Actividad", activityLevel)
            SettingsOption("Idioma", language)

            Spacer(modifier = Modifier.weight(1f)) // Espacio flexible para empujar los botones hacia abajo

            // Botones "Configurar Cuenta" y "Eliminar Cuenta"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón de Configurar Cuenta
                Text(
                    text = "Configurar Cuenta",
                    fontSize = 18.sp,
                    color = Color(0xFF007ACC),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { navController.navigate("AccountSettingsScreen") }
                        .background(Color(0xFFE3F2FD), shape = CircleShape)
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                )

                // Botón de Eliminar Cuenta
                Text(
                    text = "Eliminar Cuenta",
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { /* Lógica para eliminar la cuenta */ }
                        .background(Color(0xFFEF5350), shape = CircleShape)
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                )
            }
        }

        BottomNavigationBar(navController = navController)

        if (showEditDialog) {
            EditProfileDialog(
                userName = userName,
                userLastName = userLastName,
                currentWeight = currentWeight,
                targetWeight = targetWeight,
                height = height,
                age = age,
                activityLevel = activityLevel,
                language = language,
                onDismiss = { showEditDialog = false },
                onSave = { newName, newLastName, newCurrentWeight, newTargetWeight, newHeight, newAge, newActivityLevel, newLanguage ->
                    userName = newName
                    userLastName = newLastName
                    currentWeight = newCurrentWeight
                    targetWeight = newTargetWeight
                    height = newHeight
                    age = newAge
                    activityLevel = newActivityLevel
                    language = newLanguage
                    showEditDialog = false
                }
            )
        }
    }
}





@Composable
fun SettingsOption(title: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp) // Más padding en los laterales
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF007ACC)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun EditProfileDialog(
    userName: String,
    userLastName: String,
    currentWeight: String,
    targetWeight: String,
    height: String,
    age: String,
    activityLevel: String,
    language: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(userName) }
    var lastName by remember { mutableStateOf(userLastName) }
    var weight by remember { mutableStateOf(currentWeight) }
    var goalWeight by remember { mutableStateOf(targetWeight) }
    var userHeight by remember { mutableStateOf(height) }
    var userAge by remember { mutableStateOf(age) }
    var activity by remember { mutableStateOf(activityLevel) }
    var userLanguage by remember { mutableStateOf(language) }

    // Opciones para el nivel de actividad y el idioma
    val activityOptions = listOf("Bajo", "Medio", "Alto")
    val languageOptions = listOf("Español (ES)", "Inglés (EN)")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Editar Perfil", fontWeight = FontWeight.Bold, color = Color(0xFF007ACC)) },
        text = {
            Box(
                modifier = Modifier
                    .heightIn(max = 300.dp)
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp) // Más padding en los laterales del diálogo
            ) {
                LazyColumn {
                    item { OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }) }
                    item { OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Apellido") }) }

                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = weight,
                                onValueChange = { weight = it.filter { char -> char.isDigit() } }, // Solo números
                                label = { Text("Peso Actual") },
                                modifier = Modifier.weight(1f)
                            )
                            Text(" kg", fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
                        }
                    }

                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = goalWeight,
                                onValueChange = { goalWeight = it.filter { char -> char.isDigit() } }, // Solo números
                                label = { Text("Objetivo de Peso") },
                                modifier = Modifier.weight(1f)
                            )
                            Text(" kg", fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
                        }
                    }

                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = userHeight,
                                onValueChange = { userHeight = it.filter { char -> char.isDigit() } }, // Solo números
                                label = { Text("Altura") },
                                modifier = Modifier.weight(1f)
                            )
                            Text(" cm", fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
                        }
                    }

                    item {
                        DropdownSelector(
                            label = "Nivel de Actividad",
                            options = activityOptions,
                            selectedOption = activity,
                            onOptionSelected = { activity = it }
                        )
                    }

                    item {
                        DropdownSelector(
                            label = "Idioma",
                            options = languageOptions,
                            selectedOption = userLanguage,
                            onOptionSelected = { userLanguage = it }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(name, lastName, weight, goalWeight, userHeight, userAge, activity, userLanguage) }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .background(Color.White)
                .padding(8.dp)
        ) {
            Text(text = selectedOption, color = Color.Gray)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
