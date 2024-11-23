package com.example.fitness360

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
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
import androidx.compose.ui.res.stringResource
import com.example.fitness360.network.CalculatorService
import com.example.fitness360.utils.StepCounterViewModel
import com.example.fitness360.utils.getUserUid
import kotlinx.coroutines.launch

@Composable
fun CalculatorsScreen(navController: NavController, viewModel: StepCounterViewModel) {
    val steps by viewModel.steps.collectAsState()
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
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()), // Habilitamos el desplazamiento
            horizontalAlignment = Alignment.Start
        ) {
            // Encabezado con padding superior ajustado
            Column(modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)) {
                Text(
                    text = stringResource(R.string.calculator_title),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                Text(
                    text = stringResource(R.string.macronutrients_subtitle),
                    fontSize = 22.sp,
                    color = Color(0xFF007ACC),
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Verificar que los datos del usuario estén disponibles
            userData?.let { user ->
                // Llenar los campos con los datos del usuario
                val userInfo = listOf(
                    stringResource(R.string.height) to "${user.height} cm",
                    stringResource(R.string.weight_label) to "${user.actualWeight} kg",
                    stringResource(R.string.age_label) to user.age.toString() + " " + stringResource(R.string.years_old),
                    stringResource(R.string.goal_label) to if (user.goalWeight > user.actualWeight) stringResource(R.string.gain_weight) else stringResource(R.string.lose_weight)
                )

                InfoCard(
                    title = stringResource(R.string.user_data_title),
                    items = userInfo
                )

                Spacer(modifier = Modifier.height(8.dp)) // Espacio uniforme

                val macroInfo = listOf(
                    stringResource(R.string.calories_label) to "${user.kcals} kcals",
                    stringResource(R.string.proteins_label) to "${user.proteins} g",
                    stringResource(R.string.fats_label) to "${user.fats} g",
                    stringResource(R.string.carbs_label) to "${user.carbs} g",
                    stringResource(R.string.bmi_label) to (imc ?: stringResource(R.string.loading))
                )

                InfoCard(
                    title = stringResource(R.string.macros_title),
                    items = macroInfo
                )

                Spacer(modifier = Modifier.height(8.dp))

                BMIRangesExpandableCard()

            }

            Spacer(modifier = Modifier.height(56.dp))

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


@Composable
fun BMIRangesExpandableCard() {
    var isExpanded by remember { mutableStateOf(false) } // Estado para controlar el despliegue

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(8.dp, shape = RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Título y botón de expandir
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.bmi_qualification),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Contraer" else "Expandir",
                        tint = Color(0xFF007ACC)
                    )
                }
            }

            // Subtítulo breve
            if (!isExpanded) {
                Text(
                    text = stringResource(R.string.about_bmi),
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Contenido expandido
            if (isExpanded) {
                val bmiRanges = listOf(
                    stringResource(R.string.bmi_underweight) to Color(0xFF4A90E2),
                    stringResource(R.string.bmi_normal) to Color(0xFF50E3C2),
                    stringResource(R.string.bmi_overweight) to Color(0xFFFFC107),
                    stringResource(R.string.bmi_obese) to Color(0xFFE94C4C)
                )

                bmiRanges.forEach { (range, color) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Indicador de color
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(color, color.copy(alpha = 0.6f))
                                    ),
                                    shape = RoundedCornerShape(50)
                                )
                        )
                        Spacer(modifier = Modifier.width(16.dp))

                        // Texto de rango
                        Column {
                            Text(
                                text = range,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF333333)
                            )
                        }
                    }
                }
            }
        }
    }
}




