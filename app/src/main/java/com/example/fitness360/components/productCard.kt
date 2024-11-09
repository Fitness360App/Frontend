package com.example.fitness360.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitness360.R
import com.example.fitness360.network.Food

@Composable
fun ProductCard(food: Food) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF005A9E)),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.product_image),
                        contentDescription = food.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .background(Color.White, shape = RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    )

                    // Botón de añadir
                    Box(
                        modifier = Modifier
                            .padding(top = 8.dp, end = 8.dp)
                            .size(32.dp)
                            .background(Color(0xFFFFA000), shape = RoundedCornerShape(6.dp))
                            .clickable { showDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Añadir",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Nombre del producto y marca
                Text(
                    text = "${food.name} - ${food.brand}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.padding(8.dp)
                )

                // Información nutricional dinámica
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier.weight(1f)
                        ) {
                            NutritionalInfo(label = "Carbohidratos", value = "${food.nutrients.carbs}gr/100gr")
                            Spacer(modifier = Modifier.height(8.dp))
                            NutritionalInfo(label = "Kilocalorías", value = "${food.nutrients.kcals} kcals/100gr")
                        }

                        Spacer(modifier = Modifier.width(32.dp))

                        Column(
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier.weight(1f)
                        ) {
                            NutritionalInfo(label = "Grasas", value = "${food.nutrients.fats}gr/100gr")
                            Spacer(modifier = Modifier.height(8.dp))
                            NutritionalInfo(label = "Proteínas", value = "${food.nutrients.proteins}gr/100gr")
                        }
                    }
                }
            }
        }

        // Muestra el diálogo sobre la tarjeta cuando showDialog es true
        if (showDialog) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(10.dp)), // Aplica la forma redondeada
                contentAlignment = Alignment.Center
            ) {
                AddToMealDialog(
                    onDismiss = { showDialog = false },
                    onConfirm = { mealType, quantity ->
                        // Aquí puedes manejar la lógica para añadir la cantidad a la comida seleccionada
                        showDialog = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToMealDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var selectedMeal by remember { mutableStateOf("Desayuno") }
    var quantity by remember { mutableStateOf("") }
    val mealOptions = listOf("Desayuno", "Almuerzo", "Merienda", "Cena")
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f) // Ajusta el ancho del diálogo
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Añadir a la comida",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF005A9E)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de tipo de comida
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                        .padding(12.dp)
                ) {
                    Text(selectedMeal)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    mealOptions.forEach { meal ->
                        DropdownMenuItem(
                            text = { Text(meal) },
                            onClick = {
                                selectedMeal = meal
                                expanded = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Campo de texto para la cantidad
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Cantidad") },
                    placeholder = { Text("Escribe la cantidad a añadir") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF005A9E),
                        cursorColor = Color(0xFF005A9E)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = Color.Gray)
                    }
                    Button(
                        onClick = { onConfirm(selectedMeal, quantity) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF005A9E))
                    ) {
                        Text("Añadir", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun NutritionalInfo(label: String, value: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp
        )
    }
}
