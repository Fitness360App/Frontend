package com.example.fitness360

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitness360.components.BottomNavigationBar
import com.example.fitness360.network.ApiClient
import com.example.fitness360.network.UserService
import com.example.fitness360.utils.getUserUid
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.fitness360.network.UserData
import com.example.fitness360.utils.clearUserUid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.fitness360.network.UserDataForModification
import com.example.fitness360.network.UserGoalsForModification
import com.example.fitness360.utils.calculateMacros


fun formatName(name: String): String {
    return name.trim().lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}


@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val uid = getUserUid(context)
    var userData by remember { mutableStateOf<UserData?>(null) }
    var loadStatus by remember { mutableStateOf("Cargando...") }
    val userService = ApiClient.retrofit.create(UserService::class.java)
    val coroutineScope = rememberCoroutineScope()


    // Estados para los campos de configuración del usuario
    var showEditDialog by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }
    var userLastName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var currentWeight by remember { mutableStateOf("") }
    var targetWeight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var activityLevel by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("Español (ES)") } // Se mantiene estático

    LaunchedEffect(uid) {
        uid?.let {
            while (true) {
                try {
                    val userResponse = userService.getUserDataByID(it)
                    if (userResponse.isSuccessful) {
                        userData = userResponse.body()
                        loadStatus = "Datos del usuario cargados"
                        userData?.let { data ->
                            userName = data.name
                            userLastName = "${data.lastName1} "
                            userEmail = data.email
                            currentWeight = data.actualWeight.toString()
                            targetWeight = data.goalWeight.toString()
                            height = data.height.toString()
                            age = data.age.toString()
                            activityLevel = data.activityLevel
                        }
                    } else {
                        loadStatus = "Error al cargar los datos del usuario."
                    }
                } catch (e: Exception) {
                    loadStatus = "Error de red: ${e.message}"
                }
                delay(10000L) // Intervalo de sondeo cada 10 segundos
            }
        }
    }

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
                        text = formatName(userName),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF007ACC)
                    )
                    Text(
                        text = formatName(userLastName),
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
            SettingsOption("Edad", "$age años")
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


                Text(
                    text = "Cerrar Sesión",
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable {
                            clearUserUid(context)
                            navController.navigate("auth") {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                            }
                        }
                        .background(Color(0xFF007ACC), shape = CircleShape)
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
                onSave = { newName, newLastName, newCurrentWeight, newTargetWeight, newHeight, newAge, newActivityLevel ->

                    // Calcular los macros
                    val macros = calculateMacros(
                        weight = newCurrentWeight.toFloat().toInt(),
                        height = newHeight.toFloat().toInt(),
                        age = newAge.toFloat().toInt(),
                        gender = "Hombre", // Asegúrate de que el género esté definido correctamente
                        activityLevel = newActivityLevel
                    )
                    val macros2 = uid?.let { UserGoalsForModification(it, macros.proteins, macros.carbs, macros.fats, macros.kcals) }

                    println(macros2)
                    coroutineScope.launch {
                        try {
                            // Crear datos de usuario actualizados
                            val updatedData = uid?.let {
                                UserDataForModification(
                                    uid = it,
                                    name = newName,
                                    lastName1 = newLastName,
                                    actualWeight = newCurrentWeight.toFloat(),
                                    goalWeight = newTargetWeight.toFloat(),
                                    height = newHeight.toFloat(),
                                    age = newAge.toInt(),
                                    activityLevel = newActivityLevel
                                )
                            }

                            // Hacer ambas llamadas al backend
                            val response1 = updatedData?.let { userService.modifyUserData(it) }
                            val response2 = macros2?.let { userService.modifyUserGoals(it) }

                            println(response2)

                            withContext(Dispatchers.Main) {
                                if (response2 != null) {
                                    if (response1 != null && response1.isSuccessful && response2.isSuccessful) {
                                        // Actualizar los datos locales después de la modificación
                                        userName = newName
                                        userLastName = newLastName
                                        currentWeight = newCurrentWeight
                                        targetWeight = newTargetWeight
                                        height = newHeight
                                        age = newAge
                                        activityLevel = newActivityLevel
                                        showEditDialog = false
                                    } else {
                                        // Manejar errores en cualquiera de las respuestas
                                        println("Error al modificar los datos: ${response1?.message() ?: "Desconocido"}")
                                        println("Error al modificar los objetivos: ${response2.message()}")
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            // Manejar errores de red u otros errores
                            println("Error de red: ${e.message}")
                        }
                    }
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
    onSave: (String, String, String, String, String, String, String) -> Unit
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
    val activityOptions = listOf("Sedentario", "Ligera", "Moderada", "Alta")
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = userAge,
                                onValueChange = { userAge = it.filter { char -> char.isDigit() } }, // Solo números
                                label = { Text("Edad") },
                                modifier = Modifier.weight(1f)
                            )
                            Text(" años", fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
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
            TextButton(onClick = {

                onSave(name, lastName, weight, goalWeight, userHeight, userAge, activity) }) {
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
