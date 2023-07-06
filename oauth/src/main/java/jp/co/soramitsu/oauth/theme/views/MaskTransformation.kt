package jp.co.soramitsu.oauth.theme.views

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class MaskTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        return maskFilter(text)
    }
}

private const val PHONE_LENGTH = 12
fun maskFilter(text: AnnotatedString): TransformedText {

    val numbers = text.text.replace("+", "")
    val trimmed = if (numbers.length >= PHONE_LENGTH) numbers.substring(0, PHONE_LENGTH) else numbers

    val annotatedString = AnnotatedString.Builder().run {
        for (i in trimmed.indices) {
            if (i == 0) {
                append("+")
            }
            append(trimmed[i])
        }
        toAnnotatedString()
    }

    val phoneNumberOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (offset <= 0) return offset
            if (offset <= PHONE_LENGTH) return offset + 1
            return PHONE_LENGTH + 1
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset <= 0) return offset
            if (offset <= PHONE_LENGTH) return offset - 1
            return PHONE_LENGTH
        }
    }

    return TransformedText(annotatedString, phoneNumberOffsetTranslator)
}
