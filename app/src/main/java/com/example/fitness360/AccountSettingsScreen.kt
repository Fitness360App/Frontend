import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun AccountSettingsScreen(navController: NavController) {
    var email by remember { mutableStateOf("acaElCrack@gmail.com") }
    var password by remember { mutableStateOf("**************") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.Start
    ) {
        // Encabezado con padding superior ajustado
        Column() {
            Text(
                text = "CONFIGURAR",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
            Text(
                text = "CUENTA",
                fontSize = 22.sp,
                color = Color(0xFF007ACC),
                fontWeight = FontWeight.SemiBold
            )
        }

        // Campo de correo electrónico
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Campo de contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Nueva Contraseña") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Campo para confirmar la nueva contraseña
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar Nueva Contraseña") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón estilo "Guardar Cambios"
        Text(
            text = "Guardar Cambios",
            fontSize = 18.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally) // Centrar el botón horizontalmente
                .clickable {
                    // Lógica para guardar el correo y la contraseña actualizados
                    navController.popBackStack() // Vuelve a la pantalla anterior
                }
                .background(Color(0xFF007ACC), shape = CircleShape)
                .padding(vertical = 8.dp, horizontal = 16.dp)
        )

        Text(
            text = "Cancelar",
            fontSize = 18.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally) // Centrar el botón horizontalmente
                .clickable {
                    navController.popBackStack() // Vuelve a la pantalla anterior
                }
                .background(Color(0xFF007ACC), shape = CircleShape)
                .padding(vertical = 8.dp, horizontal = 16.dp)
        )

    }
}
