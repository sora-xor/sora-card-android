package jp.co.soramitsu.oauth.base.compose

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import jp.co.soramitsu.oauth.feature.verify.phone.EnterPhoneNumberViewModel.Companion.PHONE_NUMBER_LENGTH

class MaskTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        return maskFilter(text)
    }
}

fun maskFilter(text: AnnotatedString): TransformedText {

    val numbers = text.text.replace("+", "")
    val trimmed = if (numbers.length >= PHONE_NUMBER_LENGTH) numbers.substring(0, PHONE_NUMBER_LENGTH) else numbers

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
            if (offset <= PHONE_NUMBER_LENGTH) return offset + 1
            return PHONE_NUMBER_LENGTH + 1
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset <= 0) return offset
            if (offset <= PHONE_NUMBER_LENGTH) return offset - 1
            return PHONE_NUMBER_LENGTH
        }
    }

    return TransformedText(annotatedString, phoneNumberOffsetTranslator)
}
