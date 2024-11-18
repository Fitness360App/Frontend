package com.example.fitness360

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AuthScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Centrar imágenes en la parte superior
        Spacer(modifier = Modifier.weight(1f)) // Espacio para empujar las imágenes hacia el centro

        // Logo superior (ícono principal)
        Image(
            painter = painterResource(id = R.drawable.fitness360icon),
            contentDescription = "Logo de Fitness360",
            modifier = Modifier
                .size(300.dp)
        )

        // Texto del logo (palabra 'Fitness360')
        Image(
            painter = painterResource(id = R.drawable.fitnessword),
            contentDescription = "Texto del logo de Fitness360",
            modifier = Modifier
                .padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.weight(1f)) // Espacio entre las imágenes y los botones



        // Botón 'Iniciar Sesión'
        Button(
            onClick = { navController.navigate("login") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray
            ),
            shape = RoundedCornerShape(50)
        ) {
            Text("Iniciar Sesión", color = Color(0xFF0066A1))
        }

        // Botón 'Registrarse'
        Button(
            onClick = { navController.navigate("register") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0066A1) // Color azul
            ),
            shape = RoundedCornerShape(50)
        ) {
            Text("Registrarse", color = Color.White)
        }

        // Enlace '¿Olvidaste tu contraseña?'
        Text(
            "¿Olvidaste tu contraseña?",
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.CenterHorizontally)
                .clickable { navController.navigate("forgotPassword") },
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
        )

        Spacer(modifier = Modifier.height(16.dp)) // Espacio adicional debajo del texto
    }
}
