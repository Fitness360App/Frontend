package com.example.fitness360

import DailyRecordRequest
import DailyRecordService
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitness360.components.CustomButton
import com.example.fitness360.components.WaveBackground
import com.example.fitness360.utils.validateMail
import com.example.fitness360.utils.validatePassword
import com.example.fitness360.utils.validateWeight
import com.example.fitness360.utils.validateHeight
import com.example.fitness360.utils.validateAge
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import com.example.fitness360.network.ApiClient
import com.example.fitness360.network.AuthService
import com.example.fitness360.network.LoginResponse
import com.example.fitness360.network.Macros
import com.example.fitness360.network.RegisterRequest
import com.example.fitness360.network.RegisterResponse
import com.example.fitness360.utils.saveUserUid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

var email = ""
var password = ""
var firstName = ""
var lastName = ""
var secondLastName = ""
var currentWeight = ""
var targetWeight = ""
var height = ""
var gender = ""
var age = ""
var activityLevel = ""
var role = "user"

@Composable
fun RegisterScreen(navController: NavController) {
    var showModal by remember { mutableStateOf(false) }
    var step by remember { mutableStateOf(0) }
    val totalSteps = 4
    val animatedProgress by animateFloatAsState(targetValue = step.toFloat() / (totalSteps - 1))
    val context = LocalContext.current

    // Variables para almacenar la información de registro

    var registerStatus by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        // Agregar WaveBackground en la parte superior
        WaveBackground(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            color = Color(0xFF0066A1)
        )

        if (!showModal) {
            RegistrationForm(navController) { showModal = true }
        } else {
            RegistrationSteps(
                navController,
                step,
                animatedProgress,
                totalSteps,
                onNext = { step += 1 },
                onPrevious = { if (step > 0) step -= 1 else { showModal = false } },
                onFinalizar = {
                    // Aquí llamamos a la lógica para finalizar el registro.
                    val registerRequest = RegisterRequest(
                        email = email,
                        password = password,
                        name = firstName,
                        lastName1 = lastName,
                        lastName2 = secondLastName,
                        actualWeight = currentWeight.toInt(),
                        goalWeight = targetWeight.toInt(),
                        height = height.toInt(),
                        age = age.toInt(),
                        gender = gender,
                        activityLevel = activityLevel,
                        role = role,
                        macros = calculateMacros(
                            weight = currentWeight.toInt(),
                            height = height.toInt(),
                            age = age.toInt(),
                            gender = gender,
                            activityLevel = activityLevel
                        )
                    )

                    // Llamada al servicio de registro
                    val authService = ApiClient.retrofit.create(AuthService::class.java)
                    authService.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
                        override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                            if (response.isSuccessful) {
                                val registerResponse = response.body()
                                if (registerResponse != null) {
                                    saveUserUid(context, registerResponse.uid)
                                    // Llamada al servicio de registro para crearlo
                                    val dailyRecodService = ApiClient.retrofit.create(DailyRecordService::class.java)

                                    // Crear el servicio de DailyRecord
                                    val dailyRecordService = ApiClient.retrofit.create(DailyRecordService::class.java)

                                    // Obtener la fecha actual en formato `dd/MM/yyyy`
                                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                    val currentDate = dateFormat.format(Date())

                                    // Crear la solicitud para DailyRecord con el UID y la fecha actual
                                    val dailyRecordRequest = DailyRecordRequest(uid = registerResponse.uid, date = currentDate)

                                    // Llamada a la API para crear el DailyRecord
                                    CoroutineScope(Dispatchers.IO).launch {
                                        try {
                                            val response = dailyRecordService.createDailyRecord(dailyRecordRequest)
                                            withContext(Dispatchers.Main) {
                                                if (response.isSuccessful) {
                                                    // Navegar a Home solo si el registro y el DailyRecord fueron exitosos
                                                    navController.navigate("home") {
                                                        popUpTo("register") { inclusive = true }
                                                    }
                                                } else {
                                                    // Manejo de error al crear el DailyRecord
                                                    registerStatus = "Error al crear el registro diario: ${response.message()}"
                                                }
                                            }
                                        } catch (e: Exception) {
                                            withContext(Dispatchers.Main) {
                                                registerStatus = "Error de red al crear el registro diario: ${e.message}"
                                            }
                                        }
                                    }
                                }
                            } else {
                                // Manejo de errores
                                registerStatus = "Registro fallido. Verifica tus datos."
                            }
                        }

                        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                            registerStatus = "Error de red: ${t.message}"
                        }
                    })
                }
            )
        }
    }
}

// Pantalla inicial de formulario de registro
@Composable
fun RegistrationSteps(
    navController: NavController,
    step: Int,
    animatedProgress: Float,
    totalSteps: Int,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onFinalizar: () -> Unit // Nueva función para cuando se hace clic en finalizar
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 250.dp, start = 24.dp, end = 24.dp)
    ) {
        LinearProgressIndicator(
            progress = animatedProgress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            color = Color(0xFF0066A1),
            trackColor = Color.LightGray
        )

        when (step) {
            0 -> StepPersonalInfo(onNext, onPrevious)
            1 -> StepWeightGoals(onNext, onPrevious)
            2 -> StepActivityInfo(onNext, onPrevious)
            3 -> StepConfirmation(navController, onPrevious, onFinalizar) // Pasamos onFinalizar
        }
    }
}

@Composable
fun RegistrationForm(navController: NavController, onRegistrationStart: () -> Unit) {
    var emailField by remember { mutableStateOf("") }
    var passwordField by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val validateForm = {
        emailError = validateMail(emailField) ?: if (emailField.isBlank()) "Este campo no puede estar vacío" else null
        passwordError = validatePassword(passwordField) ?: if (passwordField.isBlank()) "Este campo no puede estar vacío" else null
        confirmPasswordError = if (confirmPassword.isBlank()) "Este campo no puede estar vacío" else null

        if (passwordField != confirmPassword) {
            confirmPasswordError = "Las contraseñas no coinciden"
        }

        if (emailError == null && passwordError == null && confirmPasswordError == null) {
            email = emailField
            password = passwordField
            onRegistrationStart()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 300.dp, start = 24.dp, end = 24.dp)
    ) {
        Text("¡ Bienvenido/a !", fontSize = 32.sp, fontWeight = FontWeight.Bold)

        CustomOutlinedTextFieldWithValidation(
            label = "Correo Electrónico",
            value = emailField,
            onValueChange = { emailField = it },
            validation = { if (it.isBlank()) "Este campo no puede estar vacío" else null },
            shape = RoundedCornerShape(30.dp)
        )
        if (emailError != null) {
            Text(text = emailError!!, color = Color.Red, fontSize = 12.sp)
        }

        CustomOutlinedTextFieldWithValidation(
            label = "Contraseña",
            value = passwordField,
            onValueChange = { passwordField = it },
            validation = { if (it.isBlank()) "Este campo no puede estar vacío" else null },
            shape = RoundedCornerShape(30.dp),
            keyboardType = KeyboardType.Password
        )
        if (passwordError != null) {
            Text(text = passwordError!!, color = Color.Red, fontSize = 12.sp)
        }

        CustomOutlinedTextFieldWithValidation(
            label = "Confirmar Contraseña",
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            validation = { if (it.isBlank()) "Este campo no puede estar vacío" else null },
            shape = RoundedCornerShape(30.dp),
            keyboardType = KeyboardType.Password
        )
        if (confirmPasswordError != null) {
            Text(text = confirmPasswordError!!, color = Color.Red, fontSize = 12.sp)
        }

        CustomButton(
            text = "Iniciar Registro",
            onClick = validateForm,
            padding = PaddingValues(10.dp),
            fontWeight = FontWeight.SemiBold,
            width = 200.dp,
            height = 50.dp
        )
    }
}



// Paso 1: Información Personal
@Composable
fun StepPersonalInfo(onNext: () -> Unit, onPrevious: () -> Unit) {
    var firstNamefield by remember { mutableStateOf("") }
    var lastNamefield by remember { mutableStateOf("") }
    var secondLastNameField by remember { mutableStateOf("") }

    var firstNameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var secondLastNameError by remember { mutableStateOf<String?>(null) }

    val validateForm = {
        firstNameError = if (firstNamefield.isBlank()) "Este campo no puede estar vacío" else null
        lastNameError = if (lastNamefield.isBlank()) "Este campo no puede estar vacío" else null
        secondLastNameError = if (secondLastNameField.isBlank()) "Este campo no puede estar vacío" else null

        if (firstNameError == null && lastNameError == null && secondLastNameError == null) {
            firstName = firstNamefield
            lastName = lastNamefield
            secondLastName = secondLastNameField
            onNext()
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        CustomOutlinedTextFieldWithValidation(
            label = "Nombre",
            value = firstNamefield,
            onValueChange = { firstNamefield = it },
            shape = RoundedCornerShape(30.dp),
            validation = { if (it.isBlank()) "Este campo no puede estar vacío" else null }
        )
        if (firstNameError != null) {
            Text(text = firstNameError!!, color = Color.Red, fontSize = 12.sp)
        }

        CustomOutlinedTextFieldWithValidation(
            label = "Primer Apellido",
            value = lastNamefield,
            onValueChange = { lastNamefield = it },
            shape = RoundedCornerShape(30.dp),
            validation = { if (it.isBlank()) "Este campo no puede estar vacío" else null }
        )
        if (lastNameError != null) {
            Text(text = lastNameError!!, color = Color.Red, fontSize = 12.sp)
        }

        CustomOutlinedTextFieldWithValidation(
            label = "Segundo Apellido",
            value = secondLastNameField,
            onValueChange = { secondLastNameField = it },
            shape = RoundedCornerShape(30.dp),
            validation = { if (it.isBlank()) "Este campo no puede estar vacío" else null }
        )
        if (secondLastNameError != null) {
            Text(text = secondLastNameError!!, color = Color.Red, fontSize = 12.sp)
        }

        StepNavigationButtons(onNext = validateForm, onPrevious = onPrevious)
    }
}


// Paso 2: Objetivos de Peso
@Composable
fun StepWeightGoals(onNext: () -> Unit, onPrevious: () -> Unit) {
    var currentWeightField by remember { mutableStateOf("") }
    var targetWeightField by remember { mutableStateOf("") }
    var heightField by remember { mutableStateOf("") }
    var ageField by remember { mutableStateOf("") }

    var currentWeightError by remember { mutableStateOf<String?>(null) }
    var targetWeightError by remember { mutableStateOf<String?>(null) }
    var heightError by remember { mutableStateOf<String?>(null) }
    var ageError by remember { mutableStateOf<String?>(null) }

    val validateForm = {
        currentWeightError = validateWeight(currentWeightField) ?: if (currentWeightField.isBlank()) "Este campo no puede estar vacío" else null
        targetWeightError = validateWeight(targetWeightField) ?: if (targetWeightField.isBlank()) "Este campo no puede estar vacío" else null
        heightError = validateHeight(heightField) ?: if (heightField.isBlank()) "Este campo no puede estar vacío" else null
        ageError = validateAge(ageField) ?: if (ageField.isBlank()) "Este campo no puede estar vacío" else null

        if (currentWeightError == null && targetWeightError == null && heightError == null && ageError == null) {
            currentWeight = currentWeightField
            targetWeight = targetWeightField
            height = heightField
            age = ageField
            onNext()
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        CustomOutlinedTextFieldWithValidation(
            label = "Peso Actual (kg)",
            value = currentWeightField,
            onValueChange = { currentWeightField = it },
            keyboardType = KeyboardType.Number,
            shape = RoundedCornerShape(30.dp),
            validation = { currentWeightError = validateWeight(it) ?: if (it.isBlank()) "Este campo no puede estar vacío" else null; currentWeightError }
        )
        if (currentWeightError != null) {
            Text(text = currentWeightError!!, color = Color.Red, fontSize = 12.sp)
        }

        CustomOutlinedTextFieldWithValidation(
            label = "Objetivo de Peso (kg)",
            value = targetWeightField,
            onValueChange = { targetWeightField = it },
            keyboardType = KeyboardType.Number,
            shape = RoundedCornerShape(30.dp),
            validation = { targetWeightError = validateWeight(it) ?: if (it.isBlank()) "Este campo no puede estar vacío" else null; targetWeightError }
        )
        if (targetWeightError != null) {
            Text(text = targetWeightError!!, color = Color.Red, fontSize = 12.sp)
        }

        CustomOutlinedTextFieldWithValidation(
            label = "Altura (cm)",
            value = heightField,
            onValueChange = { heightField = it },
            keyboardType = KeyboardType.Number,
            shape = RoundedCornerShape(30.dp),
            validation = { heightError = validateHeight(it) ?: if (it.isBlank()) "Este campo no puede estar vacío" else null; heightError }
        )
        if (heightError != null) {
            Text(text = heightError!!, color = Color.Red, fontSize = 12.sp)
        }

        CustomOutlinedTextFieldWithValidation(
            label = "Edad",
            value = ageField,
            onValueChange = { ageField = it },
            keyboardType = KeyboardType.Number,
            shape = RoundedCornerShape(30.dp),
            validation = { ageError = validateAge(it) ?: if (it.isBlank()) "Este campo no puede estar vacío" else null; ageError }
        )
        if (ageError != null) {
            Text(text = ageError!!, color = Color.Red, fontSize = 12.sp)
        }

        StepNavigationButtons(onNext = validateForm, onPrevious = onPrevious)
    }
}



// Paso 3: Información de Actividad (con Dropdowns)
@Composable
fun StepActivityInfo(onNext: () -> Unit, onPrevious: () -> Unit) {
    var selectedGender by remember { mutableStateOf("") }
    var expandedGender by remember { mutableStateOf(false) }
    val genderOptions = listOf("Hombre", "Mujer", "Otro")

    var selectedActivityLevel by remember { mutableStateOf("") }
    var expandedActivityLevel by remember { mutableStateOf(false) }
    val activityLevelOptions = listOf("Sedentario", "Ligero", "Moderado", "Activo", "Muy Activo")

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        Text(text = "Género", fontSize = 16.sp, modifier = Modifier.padding(vertical = 8.dp))
        Box {
            Text(
                text = if (selectedGender.isEmpty()) "Seleccione género" else selectedGender,
                modifier = Modifier
                    .clickable { expandedGender = true }
                    .padding(16.dp)
                    .fillMaxWidth(),
                color = if (selectedGender.isEmpty()) Color.Gray else Color.Black
            )
            DropdownMenu(expanded = expandedGender, onDismissRequest = { expandedGender = false }) {
                genderOptions.forEach { genderOption ->
                    DropdownMenuItem(
                        text = { Text(genderOption) },
                        onClick = {
                            selectedGender = genderOption
                            gender = genderOption
                            expandedGender = false
                        }
                    )
                }
            }
        }

        Text(text = "Nivel de Actividad", fontSize = 16.sp, modifier = Modifier.padding(vertical = 8.dp))
        Box {
            Text(
                text = if (selectedActivityLevel.isEmpty()) "Seleccione nivel de actividad" else selectedActivityLevel,
                modifier = Modifier
                    .clickable { expandedActivityLevel = true }
                    .padding(16.dp)
                    .fillMaxWidth(),
                color = if (selectedActivityLevel.isEmpty()) Color.Gray else Color.Black
            )
            DropdownMenu(expanded = expandedActivityLevel, onDismissRequest = { expandedActivityLevel = false }) {
                activityLevelOptions.forEach { level ->
                    DropdownMenuItem(
                        text = { Text(level) },
                        onClick = {
                            selectedActivityLevel = level
                            activityLevel = level
                            expandedActivityLevel = false
                        }
                    )
                }
            }
        }

        StepNavigationButtons(onNext, onPrevious)
    }
}


// Paso 4: Confirmación
@Composable
fun StepConfirmation(navController: NavController, onPrevious: () -> Unit, onFinalizar: () -> Unit) {
    LocalImageExample()
    Text("¡Registro completo!", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp), // Espacio entre botones
            modifier = Modifier.fillMaxWidth() // Ocupa todo el ancho de la fila
        ) {
            CustomButton(
                text = "Anterior",
                onClick = onPrevious,
                padding = PaddingValues(10.dp),
                fontWeight = FontWeight.SemiBold,
                width = 150.dp, // Anchura personalizada
                height = 50.dp // Altura personalizada
            )

            CustomButton(
                text = "Finalizar",
                onClick = onFinalizar, // Aquí llamamos a onFinalizar
                padding = PaddingValues(10.dp),
                fontWeight = FontWeight.SemiBold,
                width = 150.dp, // Anchura personalizada
                height = 50.dp // Altura personalizada
            )
        }
    }
}

// Función de campo de texto con validación y error
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomOutlinedTextFieldWithValidation(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    validation: (String) -> String?,
    shape: RoundedCornerShape,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
            errorMessage = validation(it)
            isError = errorMessage != null
        },
        label = { Text(label, color = if (isError) Color.Red else Color.Gray) },
        isError = isError,
        shape = shape,
        singleLine = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = if (isError) Color.Red else Color(0xFF0066A1), // Color de borde cuando está enfocado
            unfocusedBorderColor = Color.Gray, // Color de borde cuando no está enfocado
            errorBorderColor = Color.Red, // Color de borde cuando hay error
            containerColor = Color.White, // Fondo blanco para el contenedor
            focusedLabelColor = Color(0xFF0066A1), // Color de la etiqueta cuando está enfocada
            unfocusedLabelColor = Color.Gray, // Color de la etiqueta cuando no está enfocada
        ),
        textStyle = TextStyle(color = Color.Black),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType)
    )

    if (isError && errorMessage != null) {
        Text(
            text = errorMessage ?: "",
            color = Color.Red,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

// Botones de navegación entre pasos
@Composable
fun StepNavigationButtons(onNext: () -> Unit, onPrevious: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
    ) {
        CustomButton(
            text = "Anterior",
            onClick = onPrevious,
            padding = PaddingValues(10.dp),
            fontWeight = FontWeight.SemiBold,
            width = 150.dp,
            height = 50.dp
        )

        CustomButton(
            text = "Siguiente",
            onClick = onNext,
            padding = PaddingValues(10.dp),
            fontWeight = FontWeight.SemiBold,
            width = 150.dp,
            height = 50.dp
        )
    }
}

@Composable
fun LocalImageExample() {
    Image(
        painter = painterResource(id = R.drawable.check), // Reemplaza "your_image" con el nombre de tu imagen en drawable
        contentDescription = "Descripción de la imagen",
        modifier = Modifier
            .requiredSize(350.dp), // Tamaño de la imagen
        contentScale = ContentScale.Crop, // Cómo se escala la imagen
    )
}

fun calculateMacros(weight: Int, height: Int, age: Int, gender: String, activityLevel: String): Macros {
    val bmr = if (gender == "Hombre") {
        10 * weight + 6.25 * height - 5 * age + 5
    } else {
        10 * weight + 6.25 * height - 5 * age - 161
    }

    val activityMultiplier = when (activityLevel) {
        "Sedentario" -> 1.2
        "Ligera" -> 1.375
        "Moderada" -> 1.55
        "Alta" -> 1.725
        else -> 1.2
    }

    val tdee = bmr * activityMultiplier

    val protein = (weight * 2).toInt()  // 2 gramos de proteína por kg de peso
    val fat = (weight * 0.8).toInt()    // 0.8 gramos de grasa por kg de peso
    val carbs = (tdee - (protein * 4 + fat * 9)) / 4  // El resto de las calorías para carbohidratos

    return Macros(protein, carbs.toInt(), fat, tdee.toInt())
}