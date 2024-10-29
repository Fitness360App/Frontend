package com.example.fitness360

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.navigation.NavController


@Composable
fun LoginScreen(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
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
                .align(Alignment.CenterHorizontally)
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