package com.abdulrahman_b.hijridatepicker.components.atoms

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

/*
it represent a button icon which has it's padding by default so has force spaces inside it
 */

@Composable
fun IconButtonComponent(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color? = null
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            painter = painterResource(id = iconRes),
            tint = iconTint ?: LocalContentColor.current,
            contentDescription = contentDescription
        )
    }
}
