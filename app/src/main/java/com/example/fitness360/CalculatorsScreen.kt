package com.example.fitness360

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitness360.components.BottomNavigationBar

@Composable
fun CalculatorsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 0.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Título principal
        Box(
            modifier = Modifier
                .padding(start = 24.dp)
                .fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = "CALCULADORA",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "MACRONUTRIENTES",
                    fontSize = 20.sp,
                    color = Color(0xFF007ACC),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tarjeta de datos introducidos
        InfoCard(
            title = "Sus datos introducidos son",
            items = listOf(
                "Altura" to "171cm",
                "Peso" to "60kg",
                "Edad" to "20 años",
                "Objetivo" to "Ganar Peso"
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tarjeta de macronutrientes
        InfoCard(
            title = "Sus macros son:",
            items = listOf(
                "Kilocalorías" to "2767 kcals",
                "Proteínas" to "207g",
                "Grasas" to "77g",
                "Carbohidratos" to "311g",
                "IMC" to "20.52"
            )
        )

        BottomNavigationBar(navController)
    }
}

@Composable
fun InfoCard(title: String, items: List<Pair<String, String>>) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent // Fondo transparente para aplicar el gradiente
        )
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF005A9E), Color(0xFF007ACC))
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Excluir el último elemento si el número de elementos es impar
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
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Si hay un número impar de elementos, centra el último
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
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}




