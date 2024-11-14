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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitness360.components.BottomNavigationBar
import com.example.fitness360.utils.getUserUid
import com.example.fitness360.network.ApiClient
import com.example.fitness360.network.EditFoodRequest
import com.example.fitness360.network.MealService
import com.example.fitness360.network.RemoveFoodRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class FoodItem(val name: String, var barcode: String, var quantity: Int, val calories: Float, val carbs: Float, val fats: Float, val proteins: Float)

@Composable
fun MealScreen(navController: NavController) {


    val context = LocalContext.current
    val uid = getUserUid(context) // Obtén el uid usando el contexto actual

    val mealService = ApiClient.retrofit.create(MealService::class.java)

    // State para cada tipo de comida
    val desayuno = remember { mutableStateListOf<FoodItem>() }
    val almuerzo = remember { mutableStateListOf<FoodItem>() }
    val merienda = remember { mutableStateListOf<FoodItem>() }
    val cena = remember { mutableStateListOf<FoodItem>() }

    var selectedFood by remember { mutableStateOf<FoodItem?>(null) }

    LaunchedEffect(uid) {
        uid?.let { userId ->
            // Lista de pares de tipo de comida y su lista correspondiente
            val mealTypes = listOf(
                "breakfast" to desayuno,
                "lunch" to almuerzo,
                "snack" to merienda,
                "dinner" to cena
            )

            // Iterar sobre cada par y cargar los alimentos
            mealTypes.forEach { (mealType, mealList) ->
                val response = mealService.getMealWithFoods(userId, mealType)
                println(response.body());
                if (response.isSuccessful) {
                    response.body()?.let { foods ->

                        mealList.addAll(foods.map { foodWrapper ->
                            FoodItem(
                                barcode = foodWrapper.food.barcode,
                                name = foodWrapper.food.name ?: "Desconocido",
                                quantity = foodWrapper.servingSize,
                                calories = foodWrapper.food.kcals,
                                carbs = foodWrapper.food.carbs,
                                fats = foodWrapper.food.fats,
                                proteins = foodWrapper.food.proteins
                            )
                        })
                    }
                }
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp, bottom = 16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.Start
        ) {
            // Encabezado
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                Text("COMIDAS", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
                Text("DIARIAS", fontSize = 22.sp, color = Color(0xFF007ACC), fontWeight = FontWeight.SemiBold)
            }

            // Mostrar cada tipo de comida con sus alimentos
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                item { MealList("Desayuno", desayuno) { selectedFood = it } }
                item { MealList("Almuerzo", almuerzo) { selectedFood = it } }
                item { MealList("Merienda", merienda) { selectedFood = it } }
                item { MealList("Cena", cena) { selectedFood = it } }
                item { Spacer(modifier = Modifier.height(56.dp)) }
            }
        }

        BottomNavigationBar(navController = navController)

        selectedFood?.let { food ->
            if (uid != null) {
                val mealType = when {
                    desayuno.contains(food) -> "breakfast"
                    almuerzo.contains(food) -> "lunch"
                    merienda.contains(food) -> "snack"
                    cena.contains(food) -> "dinner"
                    else -> ""
                }

                EditFoodDialog(
                    food = food,
                    uid = uid,
                    type = mealType, // Pasa el tipo de comida aquí
                    onDismiss = { selectedFood = null },
                    onConfirm = { quantity -> updateFoodQuantity(desayuno, almuerzo, merienda, cena, food, quantity) },
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = food.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Cantidad: ${food.quantity}g",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Calorías: ${food.calories*(food.quantity/100f)} kcal | Carbs: ${food.carbs*(food.quantity/100f)}g | Grasas: ${food.fats*(food.quantity/100f)}g | Prot: ${food.proteins*(food.quantity/100f)}g",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = Color(0xFF007ACC),
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.TopEnd)
                    .padding(end = 4.dp, top = 4.dp) // Ajuste para separación de los bordes
                    .clickable { onClick() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFoodDialog(
    food: FoodItem,
    uid: String,
    type: String, // Añadir el tipo de comida aquí
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
    onDelete: () -> Unit
) {
    var quantity by remember { mutableStateOf(food.quantity.toString()) }
    val mealService = ApiClient.retrofit.create(MealService::class.java)
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val newQuantity = quantity.toIntOrNull() ?: food.quantity
                onConfirm(newQuantity)

                coroutineScope.launch {
                    val editRequest = EditFoodRequest(
                        barcode = food.barcode,
                        uid = uid,
                        type = type, // Usa el tipo de comida aquí
                        newSize = newQuantity
                    )
                    try {
                        val response = withContext(Dispatchers.IO) {
                            mealService.editFoodInMeal(editRequest).execute()
                        }

                        if (!response.isSuccessful) {
                            println("Error al actualizar en el backend: ${response.errorBody()}")
                        } else {
                            println("Actualización exitosa en el backend")
                            onDismiss()
                        }
                    } catch (e: Exception) {
                        println("Error al realizar la solicitud: ${e.message}")
                    }
                }
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                coroutineScope.launch {
                    try {
                        val response = withContext(Dispatchers.IO) {
                            mealService.deleteFoodFromMeal(
                                barcode = food.barcode,
                                uid = uid,
                                type = type
                            ).execute()
                        }
                        if (!response.isSuccessful) {
                            println("Error al eliminar en el backend: ${response.errorBody()}")
                        } else {
                            println("Eliminación exitosa en el backend")
                            onDelete() // Llama a onDelete para eliminar el alimento de la lista local
                            onDismiss() // Cierra el diálogo
                        }
                    } catch (e: Exception) {
                        println("Error al realizar la solicitud de eliminación: ${e.message}")
                    }
                }
            }) {
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





