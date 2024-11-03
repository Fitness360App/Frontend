package com.example.fitness360

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitness360.components.BottomNavigationBar

data class FoodItem(val name: String, var quantity: Int, val calories: Int, val carbs: Int, val fats: Int, val proteins: Int)

@Composable
fun MealScreen(navController: NavController) {
    val desayuno = remember {
        mutableStateListOf(
            FoodItem("Manzana", 100, 52, 14, 0, 0),
            FoodItem("Pan Integral", 50, 130, 25, 2, 5)
        )
    }
    val almuerzo = remember {
        mutableStateListOf(
            FoodItem("Pasta", 200, 260, 52, 1, 8),
            FoodItem("Pollo a la plancha", 150, 165, 0, 3, 31)
        )
    }
    val merienda = remember {
        mutableStateListOf(
            FoodItem("Yogur", 125, 75, 8, 3, 5),
            FoodItem("Nueces", 30, 200, 4, 20, 5)
        )
    }
    val cena = remember {
        mutableStateListOf(
            FoodItem("Ensalada Mixta", 150, 120, 8, 7, 4),
            FoodItem("Salmón", 100, 208, 0, 13, 20)
        )
    }

    var selectedFood by remember { mutableStateOf<FoodItem?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 56.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(bottom = 16.dp)) {
                    Text(
                        text = "COMIDAS",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "DIARIAS",
                        fontSize = 20.sp,
                        color = Color(0xFF007ACC),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                item { MealList("Desayuno", desayuno) { selectedFood = it } }
                item { MealList("Almuerzo", almuerzo) { selectedFood = it } }
                item { MealList("Merienda", merienda) { selectedFood = it } }
                item { MealList("Cena", cena) { selectedFood = it } }
            }
        }

        BottomNavigationBar(navController = navController)

        selectedFood?.let { food ->
            EditFoodDialog(
                food = food,
                onDismiss = { selectedFood = null },
                onConfirm = { quantity ->
                    updateFoodQuantity(desayuno, almuerzo, merienda, cena, food, quantity)
                    selectedFood = null
                },
                onDelete = {
                    desayuno.remove(food)
                    almuerzo.remove(food)
                    merienda.remove(food)
                    cena.remove(food)
                    selectedFood = null
                }
            )
        }
    }
}

// Función para actualizar la cantidad de un alimento en la lista correspondiente
fun updateFoodQuantity(
    desayuno: MutableList<FoodItem>,
    almuerzo: MutableList<FoodItem>,
    merienda: MutableList<FoodItem>,
    cena: MutableList<FoodItem>,
    food: FoodItem,
    newQuantity: Int
) {
    val updatedFood = food.copy(quantity = newQuantity)
    val targetList = when {
        desayuno.contains(food) -> desayuno
        almuerzo.contains(food) -> almuerzo
        merienda.contains(food) -> merienda
        cena.contains(food) -> cena
        else -> return
    }
    val index = targetList.indexOf(food)
    if (index != -1) {
        targetList[index] = updatedFood
    }
}

@Composable
fun MealList(mealName: String, foods: List<FoodItem>, onFoodClick: (FoodItem) -> Unit) {
    Text(
        text = mealName,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF007ACC), // Color azul para los títulos de cada comida
        modifier = Modifier.padding(vertical = 8.dp)
    )

    foods.forEach { food ->
        FoodItemRow(food = food, onClick = { onFoodClick(food) })
    }
}

@Composable
fun FoodItemRow(food: FoodItem, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)), // Fondo gris claro para las tarjetas
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = food.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(
                    text = "Cantidad: ${food.quantity}g",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Calorías: ${food.calories} kcal | Carbs: ${food.carbs}g | Grasas: ${food.fats}g | Prot: ${food.proteins}g",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF007ACC))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFoodDialog(food: FoodItem, onDismiss: () -> Unit, onConfirm: (Int) -> Unit, onDelete: () -> Unit) {
    var quantity by remember { mutableStateOf(food.quantity.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onConfirm(quantity.toIntOrNull() ?: food.quantity)
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDelete) {
                Text("Eliminar")
            }
        },
        title = { Text("Editar Alimento", color = Color(0xFF007ACC)) },
        text = {
            Column {
                Text(text = "Cantidad (en gramos):")
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Cantidad") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}
