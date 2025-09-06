package com.abdulrahman_b.hijridatepicker.components.atoms


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp

@Composable
fun ActionTextButton(text: String, onClick: () -> Unit) {
    val textStyle =
        MaterialTheme.typography.displayMedium.copy(color = MaterialTheme.colorScheme.secondary)
    TextButton(onClick = onClick) {
        AtomText(
            text = text,
            fontSize = 16.sp,
            style = textStyle
        )
    }
}

