package com.example.fitness360

import DailyRecord
import DailyRecordService
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*

import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.ui.graphics.Path


import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size



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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource

import com.example.fitness360.network.UserData


import com.example.fitness360.utils.getUserUid

import com.example.fitness360.components.BottomNavigationBar
import com.example.fitness360.components.ProductCard
import com.example.fitness360.network.ApiClient

import com.example.fitness360.network.UserDataRequest
import com.example.fitness360.network.UserService
import com.example.fitness360.utils.StepCounterViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


import kotlinx.coroutines.delay

@Composable
fun HomeScreen(navController: NavController, viewModel: StepCounterViewModel) {
    val stepsState by viewModel.steps.collectAsState()
    val context = LocalContext.current
    val uid = getUserUid(context)

    var userData by remember { mutableStateOf<UserData?>(null) }
    var dailyRecord by remember { mutableStateOf<DailyRecord?>(null) }

    var ranking by remember { mutableStateOf(listOf<Pair<String, Int>>()) }


    val loadingMessage =  stringResource(R.string.loading)
    val loadedMessage =  stringResource(R.string.loaded_user_data)
    val errorloadingMessage =  stringResource(R.string.error_user_data)
    val loaded_daily_record =  stringResource(R.string.loaded_daily_record)
    val error_daily_record =  stringResource(R.string.error_daily_record)
    val error_network =  stringResource(R.string.error_network)
    var loadStatus by remember { mutableStateOf(loadingMessage) }
    val userService = ApiClient.retrofit.create(UserService::class.java)
    val dailyRecordService = ApiClient.retrofit.create(DailyRecordService::class.java)

    // Intervalo de sondeo en milisegundos (ejemplo: 10 segundos)
    val pollingInterval = 10000L

    LaunchedEffect(uid) {
        uid?.let {
            while (true) { // Bucle infinito para el sondeo
                try {
                    // Cargar datos de usuario
                    val userResponse = userService.getUserDataByID(it)
                    if (userResponse.isSuccessful) {
                        userData = userResponse.body()
                        loadStatus = loadedMessage
                    } else {
                        loadStatus = errorloadingMessage
                    }

                    val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                    val recordResponse = dailyRecordService.getDailyRecord(uid, currentDate)
                    if (recordResponse.isSuccessful) {
                        dailyRecord = recordResponse.body()
                        loadStatus = loaded_daily_record
                    } else {
                        loadStatus = error_daily_record
                    }

                    // Cargar ranking de los 3 usuarios con más pasos
                    val rankingResponse = dailyRecordService.getBestSteps(currentDate)

                    println(rankingResponse.body())
                    if (rankingResponse.isSuccessful) {
                        ranking = rankingResponse.body()?.map { record ->
                            record.name to record.steps
                        } ?: emptyList()
                    }


                } catch (e: Exception) {
                    loadStatus = error_network + e.message
                }

                // Espera antes de la próxima actualización
                delay(pollingInterval)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 56.dp) // Espacio para el BottomNavigationBar
        ) {
            item {
                Column(
                    modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.hello),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = userData?.name?.lowercase()?.replaceFirstChar { it.uppercase() }
                            ?: loadingMessage,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0066A1)
                    )
                }
            }

            // Verificar si los datos están disponibles antes de renderizar los indicadores
            item {
                userData?.let { user ->
                    dailyRecord?.let { record ->
                        MultiLayerCircularIndicators(
                            carbProgress = 0.5f,
                            proteinProgress = 0.2f,
                            fatProgress = 0.3f,
                            kcalsProgress = 0.5f,
                            userData = user,
                            dailyRecord = record
                        )
                    }
                }
            }


            item {
                Spacer(modifier = Modifier.height(24.dp))
                dailyRecord?.let { record ->
                    userData?.kcals?.let {
                        ActivitySummary(
                            steps = record.steps.toInt(),
                            burnedKcals = record.burnedKcals.toInt(),
                            consumedKcals = record.nutrients.consumedKcals.toInt(),
                            totalKcals = it.toInt()
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                StepsRanking(ranking = ranking)
                Spacer(modifier = Modifier.height(24.dp))
            }

        }

        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            BottomNavigationBar(navController = navController)
        }

    }
}



@Composable
fun VerticalFillCircle(
    fillProgress: Float, // Progreso del llenado de 0.0 a 1.0
    size: Dp = 100.dp,
    fillColor: Color = Color(0xFFFFA500), // Color de relleno
    backgroundColor: Color = Color(0xFFE4E3E3) // Color de fondo
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(size)
    ) {
        Canvas(modifier = Modifier.size(size)) {
            // Círculo de fondo
            drawCircle(
                color = backgroundColor,
                radius = size.toPx() / 2
            )

            // Círculo de relleno que se llena de abajo hacia arriba
            val progressHeight = size.toPx() * fillProgress // Altura del relleno basado en el progreso
            val topOffset = size.toPx() - progressHeight // Offset para empezar desde abajo

            clipPath(Path().apply {
                addOval(Rect(Offset.Zero, Size(size.toPx(), size.toPx())))
            }) {
                drawRect(
                    color = fillColor,
                    topLeft = Offset(0f, topOffset),
                    size = Size(size.toPx(), progressHeight)
                )
            }
        }

    }
}

@Composable
fun MultiLayerCircularIndicators(
    carbProgress: Float,
    proteinProgress: Float,
    fatProgress: Float,
    kcalsProgress: Float,
    userData: UserData,
    dailyRecord: DailyRecord
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 16.dp)
    ) {
        println(dailyRecord.nutrients)
        // Datos de nutrientes, usando los valores de dailyRecord
        val nutrients = listOf(
            Triple(stringResource(R.string.carbs), "${dailyRecord.nutrients.consumedCarbs.toInt()}/${userData.carbs}gr", Color(0xFF7FB2D0)),
            Triple(stringResource(R.string.proteins), "${dailyRecord.nutrients.consumedProteins.toInt()}/${userData.proteins}gr", Color(0xFF3385B4)),
            Triple(stringResource(R.string.fats), "${dailyRecord.nutrients.consumedFats.toInt()}/${userData.fats}gr", Color(0xFF05476D))
        )

        Column(horizontalAlignment = Alignment.Start) {
            nutrients.forEach { (name, amount, color) ->
                NutrientInfo(name, amount, color)
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(200.dp)
        ) {
            // Fondo circular
            DrawBackgroundCircle(size = 200.dp, color = Color(0xFFE4E3E3))

            // Datos de progreso de los nutrientes y tamaños de los anillos
            val progresses = listOf(
                Pair(dailyRecord.nutrients.consumedCarbs/userData.carbs, 184.dp to Color(0xFF7FB2D0)),
                Pair(dailyRecord.nutrients.consumedProteins/userData.proteins, 152.dp to Color(0xFF3385B4)),
                Pair(dailyRecord.nutrients.consumedFats/userData.fats, 120.dp to Color(0xFF05476D))
            )

            // Dibujar cada indicador de progreso
            progresses.forEach { (progress, sizeColor) ->
                DrawProgressArc(
                    progress = progress,
                    size = sizeColor.first,
                    color = sizeColor.second
                )
            }

            // Indicador central de calorías con relleno
            VerticalFillCircle(
                fillProgress = (dailyRecord.nutrients.consumedKcals - dailyRecord.burnedKcals) / userData.kcals,
                size = 104.dp, // Tamaño del círculo
                fillColor = Color(0xFFFFA500), // Color del relleno
                backgroundColor = Color(0xFFFFFFFF) // Color de fondo
            )

            // Círculo blanco más pequeño en el centro
            DrawBackgroundCircle(size = 60.dp, color = Color(0xFFFFFFFF))

            // Texto de porcentaje en el centro
            Text(
                text = "${maxOf(0, ((dailyRecord.nutrients.consumedKcals - dailyRecord.burnedKcals) / userData.kcals * 100).toInt())}%",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun DrawBackgroundCircle(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        drawCircle(color = color, radius = size.toPx() / 2)
    }
}

@Composable
fun DrawProgressArc(progress: Float, size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 360 * progress,
            useCenter = false,
            style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
        )
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
fun ActivitySummary(steps: Int, burnedKcals: Int, consumedKcals: Int, totalKcals: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp), // Ajusta la altura de la tarjeta según lo desees
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent // Fondo transparente para aplicar el degradado en el Box
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(40.dp) // Esquinas redondeadas
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0066A1), // Color inicial
                            Color(0xFF0096D1)  // Color final
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(0f, Float.POSITIVE_INFINITY) // Degradado vertical
                    )
                )
        ) {
            // Aquí comienza el contenido del Card

            Row(
                modifier = Modifier
                    .fillMaxSize(),
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    modifier = Modifier
                        .weight(0.35f) // 30% de ancho para la columna
                        .fillMaxHeight()
                        .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    // Resumen de actividad
                    ActivitySummaryItem(Icons.Default.DirectionsWalk, "$steps", stringResource(R.string.steps_label))
                    Spacer(modifier = Modifier.height(16.dp))
                    ActivitySummaryItem(Icons.Default.LocalFireDepartment, "$burnedKcals", stringResource(R.string.kcals_label))
                    Spacer(modifier = Modifier.height(16.dp))
                    ActivitySummaryItem(Icons.Default.Fastfood, "$consumedKcals", stringResource(R.string.kcals_label))
                }

                Canvas(
                    modifier = Modifier
                        .weight(0.65f) // 70% de ancho para el Canvas
                        .fillMaxHeight()
                ) {
                    val path = Path().apply {
                        moveTo(size.width * 0.1f, size.height)

                        // Primera onda más corta
                        cubicTo(
                            size.width * 0.9f, size.height * 0.8f,
                            size.width * 0.2f, size.height * 0.4f,
                            size.width, size.height * 0f
                        )
                    }

                    // Dibuja el Path primero
                    drawPath(
                        path = path,
                        color = Color.White,
                        style = Stroke(width = 10f)
                    )

                    // Dibuja el círculo naranja sobre el Path
                    drawCircle(
                        color = Color(0xFFFFA500), // Color naranja
                        radius = 15.dp.toPx(),      // Radio del círculo, ajusta el tamaño según tus necesidades
                        center = Offset(size.width * 0.72f, size.height * 0.2f) // Posición central del círculo
                    )

                    drawCircle(
                        color = Color(0xFFFFFFFF), // Color naranja
                        radius = 10.dp.toPx(),      // Radio del círculo, ajusta el tamaño según tus necesidades
                        center = Offset(size.width * 0.72f, size.height * 0.2f) // Posición central del círculo
                    )
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 30.dp, bottom = 10.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                BarSet(backgroundHeight = 100.dp, overlayHeight = 80.dp)
                Spacer(modifier = Modifier.width(8.dp))
                BarSet(backgroundHeight = 150.dp, overlayHeight = 100.dp)
                Spacer(modifier = Modifier.width(8.dp))
                BarSet(backgroundHeight = 175.dp, overlayHeight = 150.dp)
            }

        }
    }

}

@Composable
fun BarSet(backgroundHeight: Dp, overlayHeight: Dp) {
    Box(contentAlignment = Alignment.BottomCenter) {
        // Barra de fondo
        Box(
            modifier = Modifier
                .width(12.dp)
                .height(backgroundHeight)
                .background(color = Color(0xFFB0BEC5), shape = RoundedCornerShape(5.dp))
        )
        // Barra amarilla sobrepuesta
        Box(
            modifier = Modifier
                .width(12.dp)
                .height(overlayHeight)
                .background(color = Color(0xFFFFA100), shape = RoundedCornerShape(5.dp))
                .zIndex(1f)
        )
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
        Spacer(modifier = Modifier.width(5.dp)) // Espacio entre el icono y el texto
        Column {
            Text(
                text = value,
                color = Color.White,
                fontSize = 28.sp,
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
fun StepsRanking(ranking: List<Pair<String, Int>>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp), // Ajusta la altura según lo necesites
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent // Fondo transparente para aplicar el degradado
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(40.dp) // Bordes redondeados
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0066A1), // Color inicial
                            Color(0xFF0096D1)  // Color final
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(0f, Float.POSITIVE_INFINITY) // Degradado vertical
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título del ranking
                Text(
                    text = stringResource(R.string.steps_ranking),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Lista de usuarios con sus posiciones
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(ranking) { index, user ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                        ) {
                            // Icono de medalla según el puesto
                            Icon(
                                imageVector = when (index) {
                                    0 -> Icons.Default.Star // Estrella para el primer lugar
                                    1 -> Icons.Default.EmojiEvents // Trofeo para el segundo lugar
                                    else -> Icons.Default.MilitaryTech // Medalla para el tercer lugar
                                },
                                contentDescription = null,
                                tint = when (index) {
                                    0 -> Color(0xFFFFD700) // Oro
                                    1 -> Color(0xFFC0C0C0) // Plata
                                    else -> Color(0xFFCD7F32) // Bronce
                                },
                                modifier = Modifier.size(36.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            // Nombre y pasos del usuario
                            Column(
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = user.first.capitalize(), // Nombre del usuario
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${user.second} ${stringResource(R.string.steps_label)}", // Pasos
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Light,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}





