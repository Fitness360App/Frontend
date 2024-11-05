package com.example.fitness360


import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController




import com.example.fitness360.components.CustomButton
import com.example.fitness360.components.WaveBackground

import androidx.compose.runtime.*


import com.example.fitness360.network.ApiClient
import com.example.fitness360.network.AuthService

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.fitness360.components.CustomButton

import com.example.fitness360.network.LoginRequest
import com.example.fitness360.network.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import com.example.fitness360.utils.saveUserUid
import com.example.fitness360.utils.getUserUid



@Composable
fun LoginScreen(navController: NavController) {

    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginStatus by remember { mutableStateOf("") }

    // Crear instancia de AuthService usando Retrofit
    val authService = ApiClient.retrofit.create(AuthService::class.java)

    Box(
        modifier = Modifier
            .fillMaxSize() // Ocupa todo el tamaño de la pantalla
    ) {
        WaveBackground(
            modifier = Modifier,
            height = 200.dp,
            color = Color(0xFF0066A1) // Puedes personalizar el color
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally, // Centra el contenido horizontalmente
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 250.dp, start = 24.dp, end = 24.dp)
        ) {

            Text(
                text = "¡ Bienvenido/a !",
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                modifier = Modifier
                    .padding(bottom = 20.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Usuario") },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(30.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(30.dp),
                visualTransformation = PasswordVisualTransformation()
            )

            Text(
                text = "¿Has olvidado la contraseña?",
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clickable { /* Acción para recuperar contraseña */ },
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
            )

            // Uso del botón personalizado
            CustomButton(
                text = "Iniciar Sesión",
                onClick = {

                    val loginRequest = LoginRequest(email, password)
                    val call = authService.login(loginRequest)

                    // Realizar la llamada de autenticación en Retrofit
                    call.enqueue(object : Callback<LoginResponse> {
                        override fun onResponse(
                            call: Call<LoginResponse>,
                            response: Response<LoginResponse>
                        ) {
                            if (response.isSuccessful) {
                                val loginResponse = response.body()
                                if (loginResponse != null) {
                                    saveUserUid(context, loginResponse.uid)
                                    navController.navigate("home") // Redirige si el login es exitoso
                                }
                            } else {
                                loginStatus = "Login fallido. Verifica tus credenciales."
                            }
                        }

                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            loginStatus = "Error de red: ${t.message}"
                        }
                    })

                }
            )

            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = "¿Eres nuevo? ",
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(
                    text = "Regístrate",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable { navController.navigate("register") },
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            // Mostrar el estado del login
            Text(
                text = loginStatus,
                modifier = Modifier.padding(top = 16.dp),
                color = Color.Red
            )
        }
    }



}
