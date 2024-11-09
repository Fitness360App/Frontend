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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.fitness360.network.ApiClient


import com.example.fitness360.components.BottomNavigationBar
import com.example.fitness360.components.ProductCard
import com.example.fitness360.network.Food
import com.example.fitness360.network.FoodService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFoodScreen(navController: NavController) {
    val foodService = ApiClient.retrofit.create(FoodService::class.java)
    var searchQuery by remember { mutableStateOf("") }
    var foodResults by remember { mutableStateOf<List<Food>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    var noResults by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 56.dp) // Espacio reservado para el BottomNavigationBar
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp)
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
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
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    coroutineScope.launch {
                                        try {
                                            val response = foodService.searchFoodByName(searchQuery)
                                            println(response);
                                            if (response.isSuccessful) {
                                                foodResults = response.body() ?: emptyList()
                                                noResults = foodResults.isEmpty()
                                            } else {
                                                noResults = true
                                            }
                                        } catch (e: Exception) {
                                            noResults = true
                                        }
                                    }
                                }
                        )
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .background(color = Color(0xFFE4E3E3), shape = RoundedCornerShape(8.dp)),
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(color = Color(0xFFE4E3E3), shape = RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CameraAlt,
                        contentDescription = "Escanear",
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                // Aquí se podría implementar la funcionalidad de escaneo
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (noResults) {
                // Mostrar pantalla de "No se encontraron resultados"
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "No se encontraron resultados",
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No se han encontrado resultados",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                // Mostrar la lista de productos encontrados
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(foodResults.size) { index ->
                        ProductCard(food = foodResults[index])
                    }
                }
            }
        }

        // Agregar el BottomNavigationBar sin el modifier, en la parte inferior del Box
        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            BottomNavigationBar(navController)
        }
    }
}






