package com.abdulrahman_b.hijridatepicker.components.atoms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp


@Composable
fun AtomTextWithClearIcon(
    text: String = "",
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    fontSize: TextUnit = TextUnit.Unspecified,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    fontFamily: FontFamily? = null,
    overflow: TextOverflow? = null,
    maxLines: Int = Int.MAX_VALUE,
    fontWeight: FontWeight? = null,
    minLines: Int = 1,
    showClearIcon: Boolean = false,
    onClearClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AtomText(
            text = text,
            style = style,
            fontSize = fontSize,
            color = color,
            modifier = Modifier.weight(1f),
            fontWeight = fontWeight ?: FontWeight.Normal,
            overflow = overflow ?: TextOverflow.Visible,
            textAlign = textAlign,
            fontFamily = fontFamily,
            maxLines = maxLines,
            minLines = minLines
        )

        if (showClearIcon) {
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(18.dp)
                    .clickable { onClearClick?.invoke() },
                contentAlignment = Alignment.Center
            ) {
                IconAtom(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear",
                    tintColor = Color.Gray
                )
            }
        }
    }
}