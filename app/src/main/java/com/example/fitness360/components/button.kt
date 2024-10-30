package com.example.fitness360.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp


@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color(0xFF0066A1),
    textColor: Color = Color.White,
    shape: Shape = RoundedCornerShape(50),
    padding: PaddingValues = PaddingValues(top = 16.dp),
    fontSize: Int = 16,
    fontWeight: FontWeight = FontWeight.Bold,
    width: Dp = Dp.Unspecified, // Anchura personalizada
    height: Dp = 48.dp // Altura predeterminada personalizable
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(padding)
            .then(if (width != Dp.Unspecified) Modifier.width(width) else Modifier.fillMaxWidth()) // Aplicar ancho condicionalmente
            .height(height), // Aplicar altura
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        shape = shape
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = fontSize.sp,
            fontWeight = fontWeight
        )
    }
}