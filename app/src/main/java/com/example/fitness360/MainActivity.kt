package com.example.fitness360

import AccountSettingsScreen
import android.content.pm.PackageManager
import android.Manifest
import android.app.Application
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fitness360.ui.theme.Fitness360Theme
import com.example.fitness360.utils.StepCounterViewModel
import com.example.fitness360.utils.StepCounterViewModelFactory
import com.example.fitness360.utils.getUserUid
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestActivityRecognitionPermission()  // Solicitar permiso

        setContent {
            Fitness360Theme {
                MainScreen()
            }
        }
    }

    private fun requestActivityRecognitionPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            // Manejar el caso en que el permiso no fue concedido
            //Toast.makeText(context, "Funcionalidad de los pasos", Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val uid = getUserUid(context)
    println("UID: $uid")
    lateinit var cameraExecutor: ExecutorService
    cameraExecutor = Executors.newSingleThreadExecutor()

    val stepCounterViewModel: StepCounterViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = StepCounterViewModelFactory(context.applicationContext as Application,
            context
        )
    )

    val startDestination = if (uid == null) "auth" else "home"

    NavHost(navController, startDestination = startDestination) {
        composable("auth") { AuthScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("forgotPassword") { ForgotPasswordScreen(navController) }
        composable("home") { HomeScreen(navController, viewModel = stepCounterViewModel) }
        composable("search") { SearchFoodScreen(navController, viewModel = stepCounterViewModel, cameraExecutor) }
        composable("calculators") { CalculatorsScreen(navController, viewModel = stepCounterViewModel) }
        composable("meal") { MealScreen(navController, viewModel = stepCounterViewModel) }
        composable("settings") { SettingsScreen(navController, viewModel = stepCounterViewModel) }
        composable("AccountSettingsScreen") { AccountSettingsScreen(navController, viewModel = stepCounterViewModel) }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Fitness360Theme {
        Greeting("Android")
    }
}
