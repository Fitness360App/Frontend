package com.example.fitness360

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings


import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*

import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.ui.graphics.Path




import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.filled.Assignment

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.zIndex
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Column(
            modifier = Modifier.padding(start = 32.dp, top = 16.dp)
        ) {
            Text(
                text = "Hola",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Aca",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0066A1)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        MultiLayerCircularIndicators(
            carbProgress = 0.5f,
            proteinProgress = 0.2f,
            fatProgress = 0.3f
        )

        Spacer(modifier = Modifier.height(24.dp))

        ActivitySummary()

        Spacer(modifier = Modifier.height(24.dp))

        BottomNavigationBar()
    }
}

@Composable
fun MultiLayerCircularIndicators(
    carbProgress: Float,
    proteinProgress: Float,
    fatProgress: Float
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 16.dp)
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            NutrientInfo("Carbohidratos", "50/110gr", Color(0xFF7FB2D0))
            NutrientInfo("Proteínas", "30/90gr", Color(0xFF3385B4))
            NutrientInfo("Grasas", "25/40gr", Color(0xFF05476D))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(200.dp)
        ) {
            Canvas(modifier = Modifier.size(166.dp)) {
                drawCircle(
                    color = Color(0xFFE4E3E3),
                    radius = size.minDimension / 2
                )
            }

            Canvas(modifier = Modifier.size(150.dp)) {
                drawArc(
                    color = Color(0xFF7FB2D0),
                    startAngle = -90f,
                    sweepAngle = 360 * carbProgress,
                    useCenter = false,
                    style = Stroke(width = 16.dp.toPx())
                )
            }

            Canvas(modifier = Modifier.size(120.dp)) {
                drawArc(
                    color = Color(0xFF3385B4),
                    startAngle = -90f,
                    sweepAngle = 360 * proteinProgress,
                    useCenter = false,
                    style = Stroke(width = 16.dp.toPx())
                )
            }

            Canvas(modifier = Modifier.size(90.dp)) {
                drawArc(
                    color = Color(0xFF05476D),
                    startAngle = -90f,
                    sweepAngle = 360 * fatProgress,
                    useCenter = false,
                    style = Stroke(width = 16.dp.toPx())
                )
            }
        }
    }
}

@Composable
fun NutrientInfo(name: String, value: String, color: Color) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = name,
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(text = value, color = Color.Black, fontSize = 14.sp, modifier = Modifier.padding(start = 14.dp))
    }
}



@Composable
fun ActivitySummary() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp), // Ajusta la altura de la tarjeta según lo desees
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0066A1) // Color de fondo azul
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(40.dp) // Esquinas redondeadas
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                verticalAlignment = Alignment.Bottom // Alinear las barras en la parte inferior
            ) {
                Column(
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.Top, // Manejar el espaciado manualmente
                    horizontalAlignment = Alignment.Start // Alinear al inicio
                ) {
                    // Resumen de actividad
                    ActivitySummaryItem(Icons.Default.DirectionsWalk, "4000", "pasos") // Icono de pasos
                    Spacer(modifier = Modifier.height(20.dp)) // Espacio entre elementos
                    ActivitySummaryItem(Icons.Default.LocalFireDepartment, "352", "kcals") // Icono de fuego para kcal
                    Spacer(modifier = Modifier.height(20.dp)) // Espacio entre elementos
                    ActivitySummaryItem(Icons.Default.Feed, "500", "kcals") // Usar un icono alternativo
                }

                Canvas(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    val path = Path().apply {
                        moveTo(size.width * 0.1f, size.height) // Punto inicial en la parte inferior

                        // Primera onda más corta
                        cubicTo(
                            size.width * 0.9f, size.height * 0.8f, // Primer punto de control
                            size.width * 0.2f, size.height * 0.4f, // Segundo punto de control
                            size.width, size.height * 0f // Punto final en la parte superior derecha
                        )
                    }

                    drawPath(
                        path = path,
                        color = Color.White,
                        style = Stroke(width = 10f) // Grosor de la línea
                    )
                }
            }


            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd) // Alinear al final de la tarjeta
                    .padding(end = 30.dp, bottom = 10.dp), // Espaciado desde el borde
                verticalAlignment = Alignment.Bottom // Alinear las barras en la parte inferior
            ) {
                // Conjunto de barras 1
                Box(
                    contentAlignment = Alignment.BottomCenter // Alinea las barras en la parte inferior
                ) {
                    // Barra de fondo
                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .height(100.dp) // Ajusta la altura según tus datos
                            .background(color = Color(0xFFB0BEC5), shape = RoundedCornerShape(5.dp)) // Color gris para fondo
                    )
                    // Barra amarilla sobrepuesta
                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .height(80.dp)
                            .background(color = Color(0xFFFFC107), shape = RoundedCornerShape(5.dp)) // Amarillo
                            .zIndex(1f) // Asegura que esté sobrepuesta
                    )
                }

                Spacer(modifier = Modifier.width(8.dp)) // Espacio entre barras

                // Conjunto de barras 2
                Box(
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Barra de fondo
                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .height(150.dp)
                            .background(color = Color(0xFFB0BEC5), shape = RoundedCornerShape(5.dp))
                    )
                    // Barra amarilla sobrepuesta
                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .height(100.dp)
                            .background(color = Color(0xFFFFC107), shape = RoundedCornerShape(5.dp))
                            .zIndex(1f)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp)) // Espacio entre barras

                // Conjunto de barras 3
                Box(
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Barra de fondo
                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .height(175.dp)
                            .background(color = Color(0xFFB0BEC5), shape = RoundedCornerShape(5.dp))
                    )
                    // Barra amarilla sobrepuesta
                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .height(150.dp)
                            .background(color = Color(0xFFFFC107), shape = RoundedCornerShape(5.dp))
                            .zIndex(1f)
                    )
                }
            }

        }
    }
}

@Composable
fun ActivitySummaryItem(icon: ImageVector, value: String, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(36.dp) // Ajustar el tamaño del icono
        )
        Spacer(modifier = Modifier.width(8.dp)) // Espacio entre el icono y el texto
        Column {
            Text(
                text = value,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Light // Ajusta el peso de la fuente a "fino"
            )
        }
    }
}



@Composable
fun BottomNavigationBar() {
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
                .height(80.dp) // Ajusta la altura para hacer más grande la barra
        ) {
            IconButton(onClick = { /* Acción del ícono */ }, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Inicio",
                    modifier = Modifier.size(36.dp) // Ajusta el tamaño del icono
                )
            }
            IconButton(onClick = { /* Acción del ícono */ }, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    modifier = Modifier.size(36.dp) // Ajusta el tamaño del icono
                )
            }
            IconButton(onClick = { /* Acción del ícono de calculadora */ }, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Rounded.Calculate,
                    contentDescription = "Calculadora",
                    modifier = Modifier.size(36.dp) // Ajusta el tamaño del icono
                )
            }
            IconButton(onClick = { /* Acción del ícono */ }, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Default.Assignment,
                    contentDescription = "Listas",
                    modifier = Modifier.size(36.dp) // Ajusta el tamaño del icono
                )
            }
            IconButton(onClick = { /* Acción del ícono */ }, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Configuración",
                    modifier = Modifier.size(36.dp) // Ajusta el tamaño del icono
                )
            }
        }
    }
}