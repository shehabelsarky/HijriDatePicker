package com.abdulrahman_b.hijridatepicker.components.atoms

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.abdulrahman_b.hijridatepicker.R

@Composable
fun SheetHeader(
    label: String,
    labelModifier: Modifier? = null,
    labelColor: Color? = null,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButtonComponent(
                onClick = onDismiss,
                iconRes = R.drawable.ic_close,
                contentDescription = stringResource(R.string.close),
                modifier = Modifier.size(24.dp),
                iconTint = Color.Unspecified
            )

            Spacer(modifier = Modifier.weight(1f))

            ActionTextButton(
                text = stringResource(R.string.done),
                onClick = onSave
            )
        }

        // Centered label over the Row
        val displayLabel = if (label.length > 25) label.take(25) + "..." else label
        LabelField(
            text = displayLabel,
            isBold = true,
            color = labelColor ?: MaterialTheme.colorScheme.primary,
            modifier = (labelModifier ?: Modifier).align(Alignment.Center)
        )
    }
}
