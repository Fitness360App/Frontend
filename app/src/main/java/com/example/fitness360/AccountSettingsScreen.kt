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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitness360.network.ApiClient
import com.example.fitness360.network.UserChangePasswordRequest
import com.example.fitness360.network.UserSendEmailRequest
import com.example.fitness360.network.UserService
import com.example.fitness360.utils.clearUserUid
import com.example.fitness360.utils.getUserUid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.fitness360.utils.validatePassword

@Composable
fun AccountSettingsScreen(navController: NavController) {
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
                    text = "CONFIGURAR",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                Text(
                    text = "CUENTA",
                    fontSize = 22.sp,
                    color = Color(0xFF007ACC),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = "Volver",
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
            label = { Text("Nueva Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo confirmar nueva contraseña
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar Nueva Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {


            Text (
                text = "Guardar Cambios",
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable {
                        if (newPassword != confirmPassword) {
                            errorMessage = "Las contraseñas no coinciden"
                        } else if (newPassword.isEmpty()) {
                            errorMessage = "Todos los campos son obligatorios"
                        } else if (validatePassword(newPassword) != null) {
                            errorMessage = validatePassword(newPassword)
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
                                        verificationMessage = "Se ha enviado un correo de verificación a su correo electrónico"
                                    } else {
                                        verificationMessage = "No se pudo enviar el correo de verificación"
                                    }
                                } catch (e: Exception) {
                                    verificationMessage = "Error al enviar el correo: ${e.message}"
                                }
                            }
                        }

                    }
                    .background(Color(0xFF007ACC), shape = CircleShape)
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            )


            Text(
                text = "Eliminar Cuenta",
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
            title = { Text("Verificación de Código") },
            text = {
                Column {
                    Text(verificationMessage)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = verificationCode,
                        onValueChange = { verificationCode = it },
                        label = { Text("Ingrese el Código de Verificación") },
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
                                                Toast.makeText(context, "Contraseña cambiada exitosamente", Toast.LENGTH_LONG).show()
                                                navController.navigate("settings")
                                            }
                                        } else {
                                            errorMessage = "Error al cambiar la contraseña"
                                        }
                                    } catch (e: Exception) {
                                        withContext(Dispatchers.Main) {
                                            errorMessage =
                                                "Error al cambiar la contraseña: ${e.message}"
                                        }
                                    }
                                }
                            } else {
                                errorMessage = "El código de verificación no es válido"
                            }
                        } else {
                            errorMessage = "El campo de código no puede estar vacío"
                        }
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                Button(onClick = { showVerificationDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Pop-up de eliminación
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Está seguro de que desea eliminar su cuenta? Esta acción no se puede deshacer.") },
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
                                    var deleteStatus = "Error de red al crear el registro diario: ${e.message}"
                                }
                            }
                        }

                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
