package com.example.fitness360.utils

import DailyRecordService
import UpdateStepsRequest
import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import com.example.fitness360.network.ApiClient
import com.example.fitness360.network.UserSendEmailRequest
import com.example.fitness360.network.UserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StepCounterViewModel(application: Application, private val uid: String) : AndroidViewModel(application), SensorEventListener {

    private val _steps = MutableStateFlow(0)
    private var accumulatedSteps = 0
    val steps: StateFlow<Int> = _steps.asStateFlow()
    private var isDone = true

    fun getUid(): String = uid

    private var sensorManager: SensorManager? = null
    private var stepSensor: Sensor? = null

    init {
        sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor != null) {
            // Si el sensor existe, registrar el Listener
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            // Mostrar mensaje o manejar caso cuando el sensor no está disponible

            //Hacer un print para mostrar en consola
            println("Sensor de pasos no disponible");

            _steps.value = -1  // Valor para indicar que el sensor no está disponible
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {

        event?.let {
            if (it.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                val totalSteps = it.values[0].toInt()

                // Calcula los pasos desde la última actualización
                val stepsSinceLastUpdate = totalSteps - _steps.value

                // Actualiza el número total de pasos
                _steps.value = totalSteps

                // Incrementa el acumulador
                accumulatedSteps += stepsSinceLastUpdate

                println("UID: $uid, Pasos acumulados: $accumulatedSteps")

                // Si se alcanzan 100 pasos, llama a la API
                if (accumulatedSteps >= 100 && isDone) {
                    // Llama a la API
                    isDone=false
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val dailyRecordService = ApiClient.retrofit.create(DailyRecordService::class.java)
                            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            val currentDate = dateFormat.format(Date())
                            val request = UpdateStepsRequest(
                                uid = uid ?: "",
                                date = currentDate,
                                steps = 100
                            )
                            val response = dailyRecordService.updateSteps(request)
                            val response2 = dailyRecordService.updateBurnedKcalsFromSteps(request)
                            println(response2)
                            println(response)
                            accumulatedSteps = 0
                            isDone=true
                        } catch (e: Exception) {
                            println("Error en actualizar los pasos: ${e.message}")
                        }
                    }

                }
            }
        }
    }



    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No se necesita implementar esto para el sensor de pasos
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager?.unregisterListener(this)
    }
}