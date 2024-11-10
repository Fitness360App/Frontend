package com.example.fitness360

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitness360.components.BottomNavigationBar
import com.example.fitness360.network.ApiClient
import com.example.fitness360.network.UserData
import com.example.fitness360.network.UserService
import androidx.compose.ui.platform.LocalContext
import com.example.fitness360.network.CalculatorService
import com.example.fitness360.utils.getUserUid
import kotlinx.coroutines.launch

@Composable
fun CalculatorsScreen(navController: NavController) {
    val context = LocalContext.current
    val uid = getUserUid(context)
    var userData by remember { mutableStateOf<UserData?>(null) }
    val userService = ApiClient.retrofit.create(UserService::class.java)
    val calculatorService = ApiClient.retrofit.create(CalculatorService::class.java)

    var imc by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Cargar datos del usuario
    LaunchedEffect(uid) {
        uid?.let {
            coroutineScope.launch {
                try {
                    val response = userService.getUserDataByID(it)
                    if (response.isSuccessful) {
                        userData = response.body()

                        userData?.let { user ->

                            // Llamada a la API para calcular el IMC
                            val imcResponse = calculatorService.calculateIMCByUserData(uid)
                            if (imcResponse.isSuccessful) {
                                imc = imcResponse.body()?.imc.toString()
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Manejar error si ocurre
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
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start
        ) {
            // Encabezado con padding superior ajustado
            Column(modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)) {
                Text(
                    text = "CALCULADORA",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                Text(
                    text = "MACRONUTRIENTES",
                    fontSize = 22.sp,
                    color = Color(0xFF007ACC),
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Verificar que los datos del usuario estén disponibles
            userData?.let { user ->
                // Llenar los campos con los datos del usuario
                val userInfo = listOf(
                    "Altura" to "${user.height} cm",
                    "Peso" to "${user.actualWeight} kg",
                    "Edad" to "${user.age} años",
                    "Objetivo" to if (user.goalWeight > user.actualWeight) "Ganar Peso" else "Perder Peso"
                )

                InfoCard(
                    title = "Sus datos introducidos son",
                    items = userInfo
                )

                Spacer(modifier = Modifier.height(16.dp)) // Espacio uniforme

                val macroInfo = listOf(
                    "Kilocalorías" to "${user.kcals} kcals",
                    "Proteínas" to "${user.proteins} g",
                    "Grasas" to "${user.fats} g",
                    "Carbohidratos" to "${user.carbs} g",
                    "IMC" to (imc ?: "Cargando...")
                )

                InfoCard(
                    title = "Sus macros son:",
                    items = macroInfo
                )
            }
        }

        // Barra de navegación en la parte inferior, fija
        BottomNavigationBar(
            navController = navController
        )
    }
}

// Función para calcular el índice de masa corporal (IMC)
fun calculateBMI(weight: Int, height: Int): Float {
    val heightInMeters = height / 100f
    return weight / (heightInMeters * heightInMeters)
}

@Composable
fun InfoCard(title: String, items: List<Pair<String, String>>) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(8.dp, shape = RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF007ACC), Color(0xFF005A9E))
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Muestra los elementos en pares o centra el último si es impar
                val itemsToDisplay = if (items.size % 2 != 0) items.dropLast(1) else items

                itemsToDisplay.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        rowItems.forEach { (label, value) ->
                            Column(
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = value,
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Si el número de elementos es impar, centra el último
                if (items.size % 2 != 0) {
                    val lastItem = items.last()
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = lastItem.first,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = lastItem.second,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}
