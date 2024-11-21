import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitness360.R
import com.example.fitness360.network.ApiClient
import com.example.fitness360.network.UserChangePasswordRequest
import com.example.fitness360.network.UserSendEmailRequest
import com.example.fitness360.network.UserService
import com.example.fitness360.utils.StepCounterViewModel
import com.example.fitness360.utils.clearUserUid
import com.example.fitness360.utils.getUserUid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.fitness360.utils.validatePassword

@Composable
fun AccountSettingsScreen(navController: NavController, viewModel: StepCounterViewModel) {
    val steps by viewModel.steps.collectAsState()
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf("") }
    var showVerificationDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var verificationMessage by remember { mutableStateOf("") }
    val userService = ApiClient.retrofit.create(UserService::class.java)
    val context = LocalContext.current
    val uid = getUserUid(context)
    var receiveCode by remember { mutableStateOf("") }
    val passwordDoNotMatchMessage = stringResource(R.string.password_mismatch)
    val passwordEmptyMessage = stringResource(R.string.empty_fields)
    val emailVerificationMessage = stringResource(R.string.verification_sent)
    val emailVerificationFailedMessage = stringResource(R.string.verification_failed)
    val passwordChangedSuccesfully = stringResource(R.string.password_changed_successfully)
    val codeNotValid = stringResource(R.string.verification_code_error)
    val codeEmpty = stringResource(R.string.empty_code_field)
    val passwordChangeError = stringResource(R.string.password_change_error)
    val emailSendingError = stringResource(R.string.error_sending_email)
    val password_less_than_8 = stringResource(R.string.password_less_than_8)
    val password_must_contain_uppercase = stringResource(R.string.password_must_contain_uppercase)
    val password_must_contain_lowercase = stringResource(R.string.password_must_contain_lowercase)
    val password_must_contain_number = stringResource(R.string.password_must_contain_number)
    val password_must_contain_special = stringResource(R.string.password_must_contain_special)
    val password_exceptions = listOf(password_less_than_8, password_must_contain_uppercase, password_must_contain_lowercase, password_must_contain_number, password_must_contain_special)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.Start
    ) {
        // Encabezado

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Column {
                Text(
                    text = stringResource(R.string.account_settings_title),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                Text(
                    text = stringResource(R.string.account_settings_subtitle),
                    fontSize = 22.sp,
                    color = Color(0xFF007ACC),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = stringResource(R.string.back),
                fontSize = 18.sp,
                color = Color(0xFF007ACC),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable {
                        navController.popBackStack() // Navega hacia atrás
                        //volver a settingsScreen

                    }
                    .padding(horizontal = 8.dp)
            )
        }




        Spacer(modifier = Modifier.height(16.dp))

        // Campo contraseña actual
        /*OutlinedTextField(
            value = currentPassword,
            onValueChange = { currentPassword = it },
            label = { Text("Contraseña Actual") },
            modifier = Modifier.fillMaxWidth()
        )*/

        // Campo nueva contraseña
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text(stringResource(R.string.new_password)) },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo confirmar nueva contraseña
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text(stringResource(R.string.confirm_new_password)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {


            Text (
                text = stringResource(R.string.confirm_new_password),
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable {
                        if (newPassword != confirmPassword) {
                            errorMessage = passwordDoNotMatchMessage
                        } else if (newPassword.isEmpty()) {
                            errorMessage = passwordEmptyMessage
                        } else if (validatePassword(newPassword, password_exceptions) != null) {
                            errorMessage = validatePassword(newPassword, password_exceptions)
                        }
                        else {
                            errorMessage = null
                            showVerificationDialog = true
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val request = UserSendEmailRequest(
                                        uid = uid ?: "",
                                        password = newPassword,
                                        oldpassword = currentPassword
                                    )
                                    val response = userService.sendEmailConfirmation(request)
                                    println(response.body())
                                    println(response)

                                    receiveCode = response.body().toString()
                                    println("Código de verificación: $receiveCode")
                                    if (response.isSuccessful) {
                                        verificationMessage = emailVerificationMessage
                                    } else {
                                        verificationMessage = emailVerificationFailedMessage
                                    }
                                } catch (e: Exception) {
                                    verificationMessage = emailSendingError + e.message
                                }
                            }
                        }

                    }
                    .background(Color(0xFF007ACC), shape = CircleShape)
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            )


            Text(
                text = stringResource(R.string.delete_account),
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { showDeleteDialog = true }
                    .background(Color(0xFFEF5350), shape = CircleShape)
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            )
        }

        // Mensaje de error
        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }

    // Pop-up de verificación
    if (showVerificationDialog) {
        AlertDialog(
            onDismissRequest = { showVerificationDialog = false },
            title = { Text(stringResource(R.string.verification_code_title)) },
            text = {
                Column {
                    Text(verificationMessage)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = verificationCode,
                        onValueChange = { verificationCode = it },
                        label = { Text(stringResource(R.string.verification_code_message)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        println("Código de verificación: $verificationCode")
                        if (verificationCode.isNotEmpty()) {
                            println("recibido: $receiveCode")
                            if (verificationCode == receiveCode) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        println("Cambiando contraseña...")
                                        val request = UserChangePasswordRequest(
                                            uid = uid ?: "",
                                            password = newPassword
                                        )
                                        val response = userService.changeUserPassword(request)
                                        println(response);
                                        println("Contraseña cambiada")
                                        if (response.isSuccessful) {
                                            withContext(Dispatchers.Main) {
                                                showVerificationDialog = false
                                                //Mostrar un mensaje de éxito
                                                Toast.makeText(context, passwordChangedSuccesfully, Toast.LENGTH_LONG).show()
                                                navController.navigate("settings")
                                            }
                                        } else {
                                            errorMessage = passwordChangeError
                                        }
                                    } catch (e: Exception) {
                                        withContext(Dispatchers.Main) {
                                            errorMessage =
                                                passwordChangeError + e.message
                                        }
                                    }
                                }
                            } else {
                                errorMessage = codeNotValid
                            }
                        } else {
                            errorMessage = codeEmpty
                        }
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                Button(onClick = { showVerificationDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    // Pop-up de eliminación
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.confirm_delete)) },
            text = { Text(stringResource(R.string.confirm_delete_account)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val response = uid?.let { userService.deleteUser(it) }
                                if (response != null) {
                                    if (response.isSuccessful) {
                                        withContext(Dispatchers.Main) {
                                            clearUserUid(context)
                                            showDeleteDialog = false
                                            navController.navigate("auth") {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    inclusive = true
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    var deleteStatus = "Error de red al borrar la cuenta: ${e.message}"
                                }
                            }
                        }

                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
