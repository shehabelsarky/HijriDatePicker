package com.abdulrahman_b.hijridatepicker.components.atoms

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
@Preview(showBackground = true)
fun LabelFieldPreview() {
    LabelField(text = "Label")
}

@Composable
fun LabelField(
    text: String,
    fontSize: Int = 16,
    isBold: Boolean = true,
    isRequired: Boolean = false,
    modifier: Modifier = Modifier.padding(top = 12.dp, bottom = 6.dp),
    color: Color = MaterialTheme.colorScheme.primary,
    textAlign: TextAlign? = null,
    toolTipInfo: String? = null
) {
    val annotatedText = buildAnnotatedString {
        append(text)

        if (isRequired) {
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.error,
                    fontSize = fontSize.sp,
                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                )
            ) {
                append(" *")
            }
        }

        if (!toolTipInfo.isNullOrBlank()) {
            appendInlineContent("tooltip", "[icon]")
        }
    }


    AtomText(
        annotatedString = annotatedText,
        fontSize = fontSize.sp,
        style = if (isBold)
            MaterialTheme.typography.displayMedium.copy(color = color)
        else
            MaterialTheme.typography.labelMedium.copy(color = color),
        textAlign = textAlign,
        modifier = modifier
    )
}




