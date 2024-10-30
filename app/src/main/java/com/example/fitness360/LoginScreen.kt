package com.example.fitness360

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import kotlin.io.path.Path
import kotlin.io.path.moveTo


import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import com.example.fitness360.components.CustomButton
import com.example.fitness360.components.WaveBackground


@Composable
fun LoginScreen(navController: NavController) {
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
                value = "",
                onValueChange = {},
                label = { Text("Usuario") },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(30.dp)
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Contraseña") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(30.dp)
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
                onClick = { /* Acción de registro */ }
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
        }
    }
}