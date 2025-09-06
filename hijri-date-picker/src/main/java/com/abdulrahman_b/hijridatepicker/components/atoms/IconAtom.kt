package com.abdulrahman_b.hijridatepicker.components.atoms

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource


@Composable
fun IconAtom(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    tintColor: Color = Color.Unspecified,
    contentDescription: String? = null
) {
    Icon(
        painter = painterResource(id = iconRes),
        contentDescription = contentDescription,
        tint = tintColor,
        modifier = modifier
    )
}

@Composable
fun IconAtom(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    tintColor: Color = Color.Unspecified,
    contentDescription: String? = null
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = tintColor,
        modifier = modifier
    )
}
