package com.example.fitness360

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavController

@Composable
fun RegisterScreen(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Nuevo Usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Confirmar Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Button(
            onClick = { /* Acción de registro */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0066A1) // Color azul
            )
        ) {
            Text("Registrarse", color = Color.White)
        }

        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp)
        ) {
            Text(
                text = "¿Ya tienes cuenta? ",
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Text(
                text = "Inicia Sesión",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .clickable { navController.navigate("login") },
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

    }
}