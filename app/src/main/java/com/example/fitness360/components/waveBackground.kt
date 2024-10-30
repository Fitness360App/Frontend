package com.example.fitness360.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun WaveBackground(
    modifier: Modifier = Modifier,
    height: Dp = 200.dp,
    color: Color = Color(0xFF0066A1)
) {
    Canvas(modifier = modifier.fillMaxWidth().height(height)) {
        val width = size.width
        val canvasHeight = size.height

        val path = Path().apply {
            moveTo(0f, canvasHeight * 0.6f) // Empezamos un poco más abajo

            // Creación de la ondulación que primero sube y luego baja
            cubicTo(
                width * 0.4f, canvasHeight * 0.2f, // Primer punto de control para subir
                width * 0.6f, canvasHeight * 1.3f, // Segundo punto de control para bajar
                width, canvasHeight * 0.8f // Punto final de la ondulación
            )

            lineTo(width, 0f)
            lineTo(0f, 0f)
            close()
        }
        drawPath(path = path, color = color, style = Fill)
    }
}
