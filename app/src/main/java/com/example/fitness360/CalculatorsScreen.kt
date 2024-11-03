package com.example.fitness360

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@Composable
fun CalculatorsScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp) // Padding uniforme en todo el Box
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
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

            InfoCard(
                title = "Sus datos introducidos son",
                items = listOf(
                    "Altura" to "171 cm",
                    "Peso" to "60 kg",
                    "Edad" to "20 años",
                    "Objetivo" to "Ganar Peso"
                )
            )

            Spacer(modifier = Modifier.height(16.dp)) // Espacio uniforme

            InfoCard(
                title = "Sus macros son:",
                items = listOf(
                    "Kilocalorías" to "2767 kcals",
                    "Proteínas" to "207 g",
                    "Grasas" to "77 g",
                    "Carbohidratos" to "311 g",
                    "IMC" to "20.52"
                )
            )
        }

        // Barra de navegación en la parte inferior, fija
        BottomNavigationBar(
            navController = navController
        )
    }
}

@Composable
fun InfoCard(title: String, items: List<Pair<String, String>>) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(8.dp, shape = RoundedCornerShape(12.dp)), // Sombra para profundidad
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
