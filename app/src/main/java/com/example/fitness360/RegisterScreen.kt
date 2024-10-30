package com.example.fitness360



import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import androidx.compose.runtime.*
import com.example.fitness360.components.CustomButton
import com.example.fitness360.components.WaveBackground
import com.example.fitness360.components.CustomOutlinedTextField
import androidx.compose.material3.LinearProgressIndicator

import androidx.compose.animation.core.animateFloatAsState

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.ui.layout.VerticalAlignmentLine


@Composable
fun RegisterScreen(navController: NavController) {
    var showModal by remember { mutableStateOf(false) }
    var step by remember { mutableStateOf(0) }
    val totalSteps = 4
    val animatedProgress by animateFloatAsState(targetValue = step.toFloat() / (totalSteps - 1))

    Box(modifier = Modifier.fillMaxSize()) {
        WaveBackground(modifier = Modifier, height = 200.dp, color = Color(0xFF0066A1))

        if (!showModal) {
            RegistrationForm(navController) { showModal = true }
        } else {
            RegistrationSteps(
                step,
                animatedProgress,
                totalSteps,
                onNext = { step += 1 },
                onPrevious = { if (step > 0) step -= 1 else { showModal = false} } // Comprobación directa aquí
            )
        }
    }
}

@Composable
fun RegistrationForm(navController: NavController, onRegisterClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 250.dp, start = 24.dp, end = 24.dp)
    ) {
        Text(
            text = "¡ Bienvenido/a !",
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        CustomOutlinedTextField(label = "Correo electrónico")
        CustomOutlinedTextField(label = "Contraseña")
        CustomOutlinedTextField(label = "Repetir contraseña")

        CustomButton(text = "Registrarse", onClick = onRegisterClick)

        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp)
        ) {
            Text("¿Ya tienes cuenta? ")
            Text(
                text = "Inicia Sesión",
                modifier = Modifier.clickable { navController.navigate("login") },
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun RegistrationSteps(step: Int, animatedProgress: Float, totalSteps: Int, onNext: () -> Unit, onPrevious: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 250.dp, start = 24.dp, end = 24.dp)
    ) {
        LinearProgressIndicator(
            progress = animatedProgress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            color = Color(0xFF0066A1),
            trackColor = Color.LightGray
        )

        when (step) {
            0 -> StepPersonalInfo(onNext, onPrevious)
            1 -> StepWeightGoals(onNext, onPrevious)
            2 -> StepActivityInfo(onNext, onPrevious)
            3 -> StepConfirmation(onPrevious)
        }
    }
}

@Composable
fun StepPersonalInfo(onNext: () -> Unit, onPrevious: () -> Unit) {
    CustomOutlinedTextField(label = "Nombre")
    CustomOutlinedTextField(label = "Primer Apellido")
    CustomOutlinedTextField(label = "Segundo Apellido")
    StepNavigationButtons(onNext = onNext, onPrevious = onPrevious)
}

@Composable
fun StepWeightGoals(onNext: () -> Unit, onPrevious: () -> Unit) {
    CustomOutlinedTextField(label = "Peso Actual")
    CustomOutlinedTextField(label = "Objetivo de Peso")
    CustomOutlinedTextField(label = "Altura")
    CustomOutlinedTextField(label = "Edad")
    StepNavigationButtons(onNext, onPrevious)
}

@Composable
fun StepActivityInfo(onNext: () -> Unit, onPrevious: () -> Unit) {
    CustomOutlinedTextField(label = "Género")
    CustomOutlinedTextField(label = "Nivel de Actividad")
    StepNavigationButtons(onNext, onPrevious)
}

@Composable
fun StepConfirmation(onPrevious: () -> Unit) {
    LocalImageExample()
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        contentAlignment = Alignment.BottomCenter


    ){
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp), // Espacio entre botones
            modifier = Modifier.fillMaxWidth() // Ocupa todo el ancho de la fila
        ) {
            CustomButton(
                text = "Anterior",
                onClick = onPrevious,
                padding = PaddingValues(10.dp),
                fontWeight = FontWeight.SemiBold,
                width = 150.dp, // Anchura personalizada
                height = 50.dp // Altura personalizada
            )

            CustomButton(
                text = "Finalizar",
                onClick = {},
                padding = PaddingValues(10.dp),
                fontWeight = FontWeight.SemiBold,
                width = 150.dp, // Anchura personalizada
                height = 50.dp // Altura personalizada
            )
        }
    }

}



@Composable
fun StepNavigationButtons(onNext: () -> Unit, onPrevious: () -> Unit, modifier: Modifier = Modifier) {


    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.BottomCenter


    ){
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp), // Espacio entre botones
            modifier = Modifier.fillMaxWidth() // Ocupa todo el ancho de la fila
                .padding(bottom = 16.dp)
        ) {
            CustomButton(
                text = "Anterior",
                onClick = onPrevious,
                padding = PaddingValues(10.dp),
                fontWeight = FontWeight.SemiBold,
                width = 150.dp, // Anchura personalizada
                height = 50.dp // Altura personalizada
            )

            CustomButton(
                text = "Siguiente",
                onClick = onNext,
                padding = PaddingValues(10.dp),
                fontWeight = FontWeight.SemiBold,
                width = 150.dp, // Anchura personalizada
                height = 50.dp // Altura personalizada
            )
        }

    }

}

@Composable
fun LocalImageExample() {
    Image(
        painter = painterResource(id = R.drawable.check), // Reemplaza "your_image" con el nombre de tu imagen en drawable
        contentDescription = "Descripción de la imagen",
        modifier = Modifier
            .requiredSize(350.dp), // Tamaño de la imagen
        contentScale = ContentScale.Crop, // Cómo se escala la imagen
    )
}