package com.example.fitness360.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings

import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Person

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BottomNavigationBar(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f)) // Empuja la barra de navegación hacia la parte inferior

        BottomAppBar(
            containerColor = Color.White,
            contentColor = Color(0xFF0066A1),
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp) // Ajusta la altura para hacer más grande la barra
        ) {
            IconButton(onClick = { navController.navigate("home") }, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Inicio",
                    modifier = Modifier.size(36.dp) // Ajusta el tamaño del icono
                )
            }
            IconButton(onClick = { navController.navigate("search")}, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    modifier = Modifier.size(36.dp) // Ajusta el tamaño del icono
                )
            }
            IconButton(onClick = { navController.navigate("calculators")}, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Rounded.Calculate,
                    contentDescription = "Calculadora",
                    modifier = Modifier.size(36.dp) // Ajusta el tamaño del icono
                )
            }
            IconButton(onClick = { navController.navigate("meal") }, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Default.Assignment,
                    contentDescription = "Listas",
                    modifier = Modifier.size(36.dp) // Ajusta el tamaño del icono
                )
            }
            IconButton(onClick = { navController.navigate("settings") }, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Configuración",
                    modifier = Modifier.size(36.dp) // Ajusta el tamaño del icono
                )
            }
        }
    }
}