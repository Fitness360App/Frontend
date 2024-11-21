package com.example.fitness360.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.fitness360.R
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun BarcodeScanner(cameraExecutor: ExecutorService, isCameraOpenSearch: Boolean, navController: NavController,onBarcodeScanned: (String) -> Unit) {
    var barcodeResult by remember { mutableStateOf("Código de barras: ") }
    var isCameraOpen by remember { mutableStateOf(isCameraOpenSearch) } // Controla si la cámara está abierta
    val errorCameraPermission = stringResource(R.string.error_camera_permission)

    val context = LocalContext.current

    // Solicitar permiso para la cámara
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isCameraOpen = true
        } else {
            barcodeResult = errorCameraPermission
        }
    }

    // Crear PreviewView y lanzar la cámara cuando isCameraOpen es verdadero
    val previewView = remember { PreviewView(context) }

    // Solo iniciar la cámara si isCameraOpen es true
    if (isCameraOpen) {
        LaunchedEffect(Unit) {
            startCamera(context, previewView, cameraExecutor) { resultText ->
                barcodeResult = resultText
                isCameraOpen = false  // Cerrar la cámara al detectar un código
                onBarcodeScanned(resultText)  // Pasar el código de barras a la pantalla principal
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isCameraOpen) {
            // Mostrar la vista de la cámara solo si está abierta
            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView({ previewView }, modifier = Modifier.fillMaxSize())  // Mostrar la vista previa de la cámara

                Text(
                    text = stringResource(R.string.scan_barcode_label),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp),
                    fontSize = 18.sp,
                    color = Color.White
                )

                // Superposición de guía con áreas oscuras alrededor del recuadro
                Box(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(250.dp, 100.dp)
                            .background(Color.White.copy(alpha = 0.3f))  // Recuadro blanco semi-transparente
                    )
                }

                // Botón para cerrar manualmente la cámara
                Button(
                    onClick = {
                        isCameraOpen = false
                        navController.navigate("search")
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(text = stringResource(R.string.close_camera))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}


// Función para iniciar CameraX
fun startCamera(
    context: Context,
    previewView: PreviewView,
    cameraExecutor: ExecutorService,
    onBarcodeDetected: (String) -> Unit
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()
        val preview = androidx.camera.core.Preview.Builder().build()
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        preview.setSurfaceProvider(previewView.surfaceProvider)

        // Configurar ImageAnalyzer para escanear el código en tiempo real
        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor, BarcodeAnalyzers { resultText ->
                    onBarcodeDetected(resultText)
                })
            }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                context as ComponentActivity,
                cameraSelector,
                preview,
                imageAnalyzer
            )
        } catch (exc: Exception) {
            exc.printStackTrace()
        }

    }, ContextCompat.getMainExecutor(context))
}

// Clase de análisis para detectar código de barras en cada frame
class BarcodeAnalyzers(private val onBarcodeDetected: (String) -> Unit) : ImageAnalysis.Analyzer {
    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val scanner = BarcodeScanning.getClient()

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    val barcodeText = barcodes.firstOrNull()?.displayValue
                    if (!barcodeText.isNullOrEmpty()) {
                        onBarcodeDetected(barcodeText)  // Llamar al callback con el valor del código de barras
                    }

                }
                .addOnFailureListener {
                    onBarcodeDetected("Error al procesar la imagen")
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}
