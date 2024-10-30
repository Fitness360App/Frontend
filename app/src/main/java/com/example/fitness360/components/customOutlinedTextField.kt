package com.example.fitness360.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CustomOutlinedTextField(label: String) {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        label = { Text(label) },

        modifier = Modifier

            .fillMaxWidth()
            .padding(top = 8.dp),
        shape = RoundedCornerShape(30.dp),

        )
}