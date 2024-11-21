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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitness360.R
import com.example.fitness360.network.AddFoodRequest
import com.example.fitness360.network.Food
import kotlinx.coroutines.launch
import com.example.fitness360.network.ApiClient
import com.example.fitness360.network.FoodService
import com.example.fitness360.network.MealService



@Composable
fun ProductCard(food: Food, uid: String) {
    var showDialog by remember { mutableStateOf(false) }

    // Capitaliza cada palabra en el nombre del producto
    val formattedName = food.name.split(" ").joinToString(" ") { word ->
        word.lowercase().replaceFirstChar { it.uppercase() }
    }

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

                // Nombre del producto y marca con formato
                Text(
                    text = "$formattedName - ${food.brand}",
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
                            NutritionalInfo(label = stringResource(R.string.carbs_label), value = "${food.nutrients.carbs.toInt()}gr/100gr")
                            Spacer(modifier = Modifier.height(8.dp))
                            NutritionalInfo(label = stringResource(R.string.calories_label), value = "${food.nutrients.kcals.toInt()} kcals/100gr")
                        }

                        Spacer(modifier = Modifier.width(32.dp))

                        Column(
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier.weight(1f)
                        ) {
                            NutritionalInfo(label = stringResource(R.string.fats_label), value = "${food.nutrients.fats.toInt()}gr/100gr")
                            Spacer(modifier = Modifier.height(8.dp))
                            NutritionalInfo(label = stringResource(R.string.carbs_label), value = "${food.nutrients.proteins.toInt()}gr/100gr")
                        }
                    }
                }
            }
        }

        // Aquí, asegúrate de que el diálogo sea independiente
        if (showDialog) {
            AddToMealDialog(
                foodBarcode = food.barcode,
                uid = uid,
                onDismiss = { showDialog = false },
                onConfirm = { mealType, quantity ->
                    showDialog = false
                }
            )
        }
    }


}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToMealDialog(
    foodBarcode: String,
    uid: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    val desayunoMessage = stringResource(R.string.breakfast)
    val almuerzoMessage = stringResource(R.string.lunch)
    val snackMessage = stringResource(R.string.snack)
    val cenaMessage = stringResource(R.string.dinner)
    var selectedMeal by remember { mutableStateOf(desayunoMessage) }
    var quantity by remember { mutableStateOf("") }
    val mealOptions = listOf(desayunoMessage, almuerzoMessage, snackMessage, cenaMessage)
    var expanded by remember { mutableStateOf(false) }
    val mealService = ApiClient.retrofit.create(MealService::class.java)

    val coroutineScope = rememberCoroutineScope() // Define el scope de corrutina

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.add),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF005A9E)
                )

                Spacer(modifier = Modifier.height(16.dp))

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

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text(stringResource(R.string.quantity)) },
                    placeholder = { Text(stringResource(R.string.write_quantity)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF005A9E),
                        cursorColor = Color(0xFF005A9E)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel), color = Color.Gray)
                    }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    // Ejecutar la solicitud en Dispatchers.IO para operaciones de red
                                    withContext(Dispatchers.IO) {
                                        val request = AddFoodRequest(
                                            barcode = foodBarcode,
                                            uid = uid,
                                            type = selectedMeal.lowercase(),
                                            servingSize = quantity.toInt()
                                        )

                                        // Realiza la solicitud en segundo plano
                                        val response = mealService.addFoodToMeal(request).execute()

                                        withContext(Dispatchers.Main) {
                                            if (response.isSuccessful) {
                                                // Llama a la función updateProcess tras una adición exitosa
                                                withContext(Dispatchers.IO) {
                                                    mealService.updateProcess(uid).execute()
                                                }

                                                onConfirm(selectedMeal, quantity)
                                            } else {
                                                println("Error en la respuesta: ${response.errorBody()}")
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    // Maneja la excepción aquí
                                    println("Error al realizar la solicitud: ${e.message}")
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF005A9E))
                    ) {
                        Text(stringResource(R.string.add), color = Color.White)
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
