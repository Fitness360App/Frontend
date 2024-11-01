package com.example.fitness360


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.navigation.NavController

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.ExperimentalMaterial3Api


import com.example.fitness360.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFoodScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 0.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp)
        ) {
            TextField(
                value = "",
                onValueChange = {},
                placeholder = {
                    Text(
                        text = "Buscar Producto",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Buscar",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)

                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp) // Altura ajustada para que coincida con el Box de la cámara
                    .background(color = Color(0xFFE4E3E3), shape = RoundedCornerShape(8.dp)),
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Caja que contiene el icono de la cámara
            Box(
                modifier = Modifier
                    .size(56.dp) // Tamaño del contenedor de la cámara para que coincida con la altura del TextField
                    .background(color = Color(0xFFE4E3E3), shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = "Escanear",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp) // Tamaño del icono dentro de la caja
                )
            }
        }

        BottomNavigationBar(navController)
    }
}
