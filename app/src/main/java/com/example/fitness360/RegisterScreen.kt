package com.example.fitness360

import DailyRecordRequest
import DailyRecordService
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.example.fitness360.network.ApiClient
import com.example.fitness360.network.AuthService
import com.example.fitness360.network.RegisterRequest
import com.example.fitness360.network.RegisterResponse
import com.example.fitness360.network.UserService
import com.example.fitness360.utils.calculateMacros
import com.example.fitness360.utils.saveUserUid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    val daily_record_error = stringResource(R.string.daily_record_error)
    val registration_error = stringResource(R.string.registration_error)
    val daily_record_network_error = stringResource(R.string.daily_record_network_error)
    val network_error = stringResource(R.string.network_error)

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

                                    println(dailyRecordRequest)
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
                                                    registerStatus = daily_record_error + response.message()
                                                }
                                            }
                                        } catch (e: Exception) {
                                            withContext(Dispatchers.Main) {
                                                registerStatus = daily_record_network_error + e.message
                                            }
                                        }
                                    }
                                }
                            } else {
                                // Manejo de errores
                                registerStatus = registration_error
                            }
                        }

                        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                            registerStatus = network_error + t.message
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
    var generalError by remember { mutableStateOf<String?>(null) }
    val userService = ApiClient.retrofit.create(UserService::class.java)

    val password_error_empty = stringResource(R.string.password_error_empty)
    val error_empty = stringResource(R.string.error_empty)
    val confirm_password_error_mismatch = stringResource(R.string.confirm_password_error_mismatch)
    val email_error_taken = stringResource(R.string.email_error_taken)
    val email_verification_error = stringResource(R.string.email_verification_error)
    val email_verification_network_error = stringResource(R.string.email_verification_network_error)
    val email_not_valid = stringResource(R.string.email_not_valid)
    val password_less_than_8 = stringResource(R.string.password_less_than_8)
    val password_must_contain_uppercase = stringResource(R.string.password_must_contain_uppercase)
    val password_must_contain_lowercase = stringResource(R.string.password_must_contain_lowercase)
    val password_must_contain_number = stringResource(R.string.password_must_contain_number)
    val password_must_contain_special = stringResource(R.string.password_must_contain_special)
    val password_exceptions = listOf(password_less_than_8, password_must_contain_uppercase, password_must_contain_lowercase, password_must_contain_number, password_must_contain_special)

    val validateForm = {
        emailError = validateMail(emailField, email_not_valid) ?: if (emailField.isBlank()) error_empty else null
        passwordError = validatePassword(passwordField, password_exceptions) ?: if (passwordField.isBlank()) password_error_empty else null
        confirmPasswordError = if (confirmPassword.isBlank()) password_error_empty else null

        if (passwordField != confirmPassword) {
            confirmPasswordError = confirm_password_error_mismatch
        }

        if (emailError == null && passwordError == null && confirmPasswordError == null) {
            CoroutineScope(Dispatchers.IO).launch  {
                try {
                    // Llamada a la API para verificar el correo
                    val response = userService.checkUserEmail(emailField)
                    if (response.isSuccessful) {
                        val isEmailTaken = response.body() ?: false
                        if (isEmailTaken) {
                            emailError = email_error_taken
                        } else {
                            generalError = null
                            email = emailField
                            password = passwordField
                            onRegistrationStart()
                        }
                    } else {
                        generalError = email_verification_error
                    }
                } catch (e: Exception) {
                    generalError = email_verification_network_error + e.message
                }
            }

        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 250.dp, start = 24.dp, end = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.welcome_message),
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp,
            modifier = Modifier
                .padding(bottom = 20.dp)
        )

        CustomOutlinedTextFieldWithValidation(
            label = stringResource(R.string.email_label),
            value = emailField,
            onValueChange = { emailField = it },
            validation = { if (it.isBlank()) error_empty else null },
            shape = RoundedCornerShape(30.dp)
        )
        if (emailError != null) {
            Text(text = emailError!!, color = Color.Red, fontSize = 12.sp)
        }

        CustomOutlinedTextFieldWithValidation(
            label = stringResource(R.string.password),
            value = passwordField,
            onValueChange = { passwordField = it },
            validation = { if (it.isBlank()) error_empty else null },
            shape = RoundedCornerShape(30.dp),
            keyboardType = KeyboardType.Password
        )
        if (passwordError != null) {
            Text(text = passwordError!!, color = Color.Red, fontSize = 12.sp)
        }

        CustomOutlinedTextFieldWithValidation(
            label = stringResource(R.string.confirm_password_label),
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            validation = { if (it.isBlank()) error_empty else null },
            shape = RoundedCornerShape(30.dp),
            keyboardType = KeyboardType.Password
        )
        if (confirmPasswordError != null) {
            Text(text = confirmPasswordError!!, color = Color.Red, fontSize = 12.sp)
        }

        CustomButton(
            text = stringResource(R.string.register_button_text),
            onClick = validateForm
        )

        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp)
        ) {
            Text(stringResource(R.string.already_have_account))
            Text(
                text = stringResource(R.string.login_button_text),
                modifier = Modifier.clickable { navController.navigate("login") },
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
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

    val error_empty = stringResource(R.string.error_empty)

    val validateForm = {
        firstNameError = if (firstNamefield.isBlank()) error_empty else null
        lastNameError = if (lastNamefield.isBlank()) error_empty else null
        secondLastNameError = if (secondLastNameField.isBlank()) error_empty else null

        if (firstNameError == null && lastNameError == null && secondLastNameError == null) {
            firstName = firstNamefield
            lastName = lastNamefield
            secondLastName = secondLastNameField
            onNext()
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        CustomOutlinedTextFieldWithValidation(
            label = stringResource(R.string.first_name_label),
            value = firstNamefield,
            onValueChange = { firstNamefield = it },
            shape = RoundedCornerShape(30.dp),
            validation = { if (it.isBlank()) error_empty else null }
        )
        if (firstNameError != null) {
            Text(text = firstNameError!!, color = Color.Red, fontSize = 12.sp)
        }

        CustomOutlinedTextFieldWithValidation(
            label = stringResource(R.string.last_name_label),
            value = lastNamefield,
            onValueChange = { lastNamefield = it },
            shape = RoundedCornerShape(30.dp),
            validation = { if (it.isBlank()) error_empty else null }
        )
        if (lastNameError != null) {
            Text(text = lastNameError!!, color = Color.Red, fontSize = 12.sp)
        }

        CustomOutlinedTextFieldWithValidation(
            label = stringResource(R.string.second_last_name_label),
            value = secondLastNameField,
            onValueChange = { secondLastNameField = it },
            shape = RoundedCornerShape(30.dp),
            validation = { if (it.isBlank()) error_empty else null }
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

    val error_empty = stringResource(R.string.error_empty)
    val invalid_weight = stringResource(R.string.invalid_weight)
    val invalid_height = stringResource(R.string.invalid_height)
    val invalid_age = stringResource(R.string.invalid_age)

    val validateForm = {
        currentWeightError = validateWeight(currentWeightField, invalid_weight) ?: if (currentWeightField.isBlank()) error_empty else null
        targetWeightError = validateWeight(targetWeightField, invalid_weight) ?: if (targetWeightField.isBlank()) error_empty else null
        heightError = validateHeight(heightField, invalid_height) ?: if (heightField.isBlank()) error_empty else null
        ageError = validateAge(ageField, invalid_age) ?: if (ageField.isBlank()) error_empty else null

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
            label = stringResource(R.string.current_weight_label),
            value = currentWeightField,
            onValueChange = { currentWeightField = it },
            keyboardType = KeyboardType.Number,
            shape = RoundedCornerShape(30.dp),
            validation = { currentWeightError = validateWeight(it, invalid_weight) ?: if (it.isBlank()) error_empty else null; currentWeightError }
        )
        if (currentWeightError != null) {
            Text(text = currentWeightError!!, color = Color.Red, fontSize = 12.sp)
        }

        CustomOutlinedTextFieldWithValidation(
            label = stringResource(R.string.target_weight_label),
            value = targetWeightField,
            onValueChange = { targetWeightField = it },
            keyboardType = KeyboardType.Number,
            shape = RoundedCornerShape(30.dp),
            validation = { targetWeightError = validateWeight(it, invalid_weight) ?: if (it.isBlank()) error_empty else null; targetWeightError }
        )
        if (targetWeightError != null) {
            Text(text = targetWeightError!!, color = Color.Red, fontSize = 12.sp)
        }

        CustomOutlinedTextFieldWithValidation(
            label = stringResource(R.string.height_label),
            value = heightField,
            onValueChange = { heightField = it },
            keyboardType = KeyboardType.Number,
            shape = RoundedCornerShape(30.dp),
            validation = { heightError = validateHeight(it, invalid_height) ?: if (it.isBlank()) error_empty else null; heightError }
        )
        if (heightError != null) {
            Text(text = heightError!!, color = Color.Red, fontSize = 12.sp)
        }

        CustomOutlinedTextFieldWithValidation(
            label = stringResource(R.string.age_label),
            value = ageField,
            onValueChange = { ageField = it },
            keyboardType = KeyboardType.Number,
            shape = RoundedCornerShape(30.dp),
            validation = { ageError = validateAge(it, invalid_age) ?: if (it.isBlank()) error_empty else null; ageError }
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
    val genderOptions = listOf(stringResource(R.string.gender_options_male), stringResource(R.string.gender_options_female), stringResource(R.string.gender_options_other))

    var selectedActivityLevel by remember { mutableStateOf("") }
    var expandedActivityLevel by remember { mutableStateOf(false) }
    val activityLevelOptions = listOf(stringResource(R.string.activity_level_sedentary), stringResource(R.string.activity_level_light), stringResource(R.string.activity_level_moderate), stringResource(R.string.activity_level_high))


    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color.Gray,
                    shape = RoundedCornerShape(30.dp)
                )
                .background(Color.White) // Fondo blanco para contraste
                .clickable { expandedGender = true } // Detectar clic para desplegar menú
                .padding(16.dp)
        ) {
            Text(
                text = if (selectedGender.isEmpty()) stringResource(R.string.gender_select) else selectedGender,
                color = if (selectedGender.isEmpty()) Color.Gray else Color.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start // Alineación a la izquierda
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

        Spacer(modifier = Modifier.height(16.dp))



        Box (
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color.Gray,
                    shape = RoundedCornerShape(30.dp)
                )
                .background(Color.White) // Fondo blanco para contraste
                .clickable { expandedActivityLevel = true } // Detectar clic para desplegar menú
                .padding(16.dp)
        ){
            Text(
                text = if (selectedActivityLevel.isEmpty()) stringResource(R.string.activity_level_label) else selectedActivityLevel,
                color = if (selectedGender.isEmpty()) Color.Gray else Color.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
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
    Text(stringResource(R.string.step_confirmation), fontSize = 24.sp, fontWeight = FontWeight.Bold)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp), // Ajusta esta distancia según sea necesario
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp) // Espaciado lateral
        ) {
            CustomButton(
                text = stringResource(R.string.previous_button_text),
                onClick = onPrevious,
                padding = PaddingValues(10.dp),
                fontWeight = FontWeight.SemiBold,
                width = 140.dp,
                height = 50.dp
            )

            CustomButton(
                text = stringResource(R.string.finish_button_text),
                onClick = onFinalizar,
                padding = PaddingValues(10.dp),
                fontWeight = FontWeight.SemiBold,
                width = 150.dp,
                height = 50.dp
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
        modifier = Modifier
            .fillMaxWidth(),
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp), // Ajusta esta distancia según sea necesario
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp) // Espaciado lateral
        ) {
            CustomButton(
                text = stringResource(R.string.previous_button_text),
                onClick = onPrevious,
                padding = PaddingValues(10.dp),
                fontWeight = FontWeight.SemiBold,
                width = 140.dp,
                height = 50.dp
            )

            CustomButton(
                text = stringResource(R.string.next_button_text),
                onClick = onNext,
                padding = PaddingValues(10.dp),
                fontWeight = FontWeight.SemiBold,
                width = 150.dp,
                height = 50.dp
            )
        }
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

