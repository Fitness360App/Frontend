package com.example.fitness360

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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


@Composable
fun LoginScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize() // Ocupa todo el tamaño de la pantalla
    ) {

        Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            val width = size.width
            val height = size.height

            val path = Path().apply {
                moveTo(0f, height * 0.6f) // Empezamos un poco más abajo

                // Creación de la ondulación que primero sube y luego baja
                cubicTo(
                    width * 0.4f, height * 0.2f, // Primer punto de control para subir
                    width * 0.6f, height*1.3f, // Segundo punto de control para bajar
                    width, height * 0.6f // Punto final de la ondulación
                )

                lineTo(width, 0f)
                lineTo(0f, 0f)
                close()
            }
            drawPath(path = path, color = Color(0xFF0066A1), style = Fill)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally, // Centra el contenido horizontalmente
            modifier = Modifier.width(300.dp) // Controla el ancho de la columna
        ) {
            Text(
                text = "¡ Bienvenido/a !",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
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

            Button(
                onClick = { /* Acción de inicio de sesión */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0066A1) // Color azul
                ),
                shape = RoundedCornerShape(50) // Botón redondeado
            ) {
                Text("Iniciar Sesión", color = Color.White)
            }

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