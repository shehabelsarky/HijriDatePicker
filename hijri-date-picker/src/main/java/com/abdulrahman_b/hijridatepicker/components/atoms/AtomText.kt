package com.abdulrahman_b.hijridatepicker.components.atoms

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit


@Composable
fun AtomText(
    text: String = "",
    inlineContent: Map<String, InlineTextContent> = emptyMap(),
    annotatedString: AnnotatedString? = null,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    fontSize: TextUnit = TextUnit.Unspecified,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    fontFamily: FontFamily? = null,
    overflow: TextOverflow? = null,
    fontWeight: FontWeight? = null,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
) {
    Text(
        text = annotatedString ?: AnnotatedString(text),
        modifier = modifier,
        inlineContent = inlineContent,
        style = style,
        fontWeight = fontWeight ?: style.fontWeight,
        fontSize = fontSize,
        color = color,
        overflow = overflow ?: TextOverflow.Visible,
        textAlign = textAlign,
        fontFamily = fontFamily,
        maxLines = maxLines,
        minLines = minLines,
    )
}